#include <stdio.h>
#include <string.h>
#include <errno.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

#include "driver/i2c.h"
#include "driver/gpio.h"          // 터치 센서용 디지털 I/O 사용
#include "esp_log.h"
#include "esp_wifi.h"
#include "esp_event.h"
#include "esp_netif.h"
#include "nvs_flash.h"
#include "lwip/sockets.h"
#include "cJSON.h"
#include "esp_timer.h"
#include <math.h>

#define I2C_MASTER_SCL_IO 22
#define I2C_MASTER_SDA_IO 21
#define I2C_MASTER_NUM I2C_NUM_0
#define I2C_MASTER_FREQ_HZ 100000

// LED GPIO 핀 정의
#define LED5_GPIO GPIO_NUM_12
#define LED4_GPIO GPIO_NUM_13
#define LED3_GPIO GPIO_NUM_14
#define LED2_GPIO GPIO_NUM_15
#define LED1_GPIO GPIO_NUM_16
#define SCORE_LISTEN_PORT 6000

static const gpio_num_t led_pins[5] = {LED1_GPIO, LED2_GPIO, LED3_GPIO, LED4_GPIO, LED5_GPIO};


#define MPU6050_ADDR_1 0x68
#define MPU6050_ADDR_2 0x69

#define WIFI_SSID    "DESKTOP-PHKCE21 0954"
#define WIFI_PASS    "P54446k]"

#define SERVER_IP    "192.168.137.226"
#define SERVER_PORT  5000

// 터치 센서 관련 정의: 외부 터치 센서는 단순 디지털 입력으로 사용
#define TOUCH_SENSOR_PIN GPIO_NUM_27
#define DEBOUNCE_TIME_MS   200      // 터치 이벤트 디바운싱 시간
#define BLANKING_TIME_MS   2000     // 측정 시작 후 2초 동안은 정지 동작 무시

static const char *TAG = "MPU6050_WIFI";
static int sock = -1;
static bool wifi_connected = false;
static bool is_measuring = false;

// 터치 센서 이벤트 관련 타이밍 (밀리초 단위)
static uint64_t last_touch_time = 0;
static uint64_t start_time = 0;

// Wi‑Fi 이벤트 핸들러 (연결, 재연결, IP 획득)
static void wifi_event_handler(void *arg, esp_event_base_t event_base,
                               int32_t event_id, void *event_data) {
    if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_START) {
        esp_wifi_connect();
    } else if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_DISCONNECTED) {
        wifi_connected = false;
        ESP_LOGI(TAG, "Wi‑Fi disconnected, trying to reconnect...");
        esp_wifi_connect();
    } else if (event_base == IP_EVENT && event_id == IP_EVENT_STA_GOT_IP) {
        wifi_connected = true;
        ip_event_got_ip_t *event = (ip_event_got_ip_t *) event_data;
        ESP_LOGI(TAG, "Got IP: " IPSTR, IP2STR(&event->ip_info.ip));
    }
}

static void wifi_init(void) {
    esp_netif_init();
    esp_event_loop_create_default();
    esp_netif_create_default_wifi_sta();

    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    esp_wifi_init(&cfg);

    esp_event_handler_instance_register(WIFI_EVENT, ESP_EVENT_ANY_ID, wifi_event_handler, NULL, NULL);
    esp_event_handler_instance_register(IP_EVENT, IP_EVENT_STA_GOT_IP, wifi_event_handler, NULL, NULL);

    wifi_config_t wifi_config = {
        .sta = {
            .ssid = WIFI_SSID,
            .password = WIFI_PASS,
        },
    };
    esp_wifi_set_mode(WIFI_MODE_STA);
    esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config);
    esp_wifi_start();
}

// 소켓 초기화 (Jetson 서버와 TCP 연결)
static void socket_init(void) {
    struct sockaddr_in dest_addr;
    memset(&dest_addr, 0, sizeof(dest_addr));
    sock = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
    if (sock < 0) {
        ESP_LOGE(TAG, "Socket creation failed, errno: %d", errno);
        return;
    }
    dest_addr.sin_family = AF_INET;
    dest_addr.sin_port = htons(SERVER_PORT);
    inet_pton(AF_INET, SERVER_IP, &dest_addr.sin_addr);

    if (connect(sock, (struct sockaddr *)&dest_addr, sizeof(dest_addr)) < 0) {
        ESP_LOGE(TAG, "Failed to connect to server, errno: %d", errno);
        close(sock);
        sock = -1;
        return;
    }
    ESP_LOGI(TAG, "Connected to server");
}

// I2C 초기화
static esp_err_t i2c_master_init(void) {
    i2c_config_t conf = {
        .mode = I2C_MODE_MASTER,
        .sda_io_num = I2C_MASTER_SDA_IO,
        .sda_pullup_en = GPIO_PULLUP_ENABLE,
        .scl_io_num = I2C_MASTER_SCL_IO,
        .scl_pullup_en = GPIO_PULLUP_ENABLE,
        .master.clk_speed = I2C_MASTER_FREQ_HZ,
    };
    return i2c_param_config(I2C_MASTER_NUM, &conf) ||
           i2c_driver_install(I2C_MASTER_NUM, conf.mode, 0, 0, 0);
}

// MPU6050 초기화 (슬립 모드 해제)
static esp_err_t mpu6050_init(uint8_t addr) {
    uint8_t data[] = {0x6B, 0x00};
    return i2c_master_write_to_device(I2C_MASTER_NUM, addr, data, sizeof(data), pdMS_TO_TICKS(100));
}

// MPU6050 가속도 데이터 읽기
static esp_err_t mpu6050_read_accel(uint8_t addr, int16_t *ax, int16_t *ay, int16_t *az) {
    uint8_t reg = 0x3B;
    uint8_t data[6];
    esp_err_t err = i2c_master_write_read_device(I2C_MASTER_NUM, addr, &reg, 1, data, sizeof(data), pdMS_TO_TICKS(100));
    if (err != ESP_OK) return err;
    *ax = (data[0] << 8) | data[1];
    *ay = (data[2] << 8) | data[3];
    *az = (data[4] << 8) | data[5];
    return ESP_OK;
}

// MPU6050 자이로 데이터를 읽는 함수 (수정됨)
static esp_err_t mpu6050_read_gyro(uint8_t addr, int16_t *gx, int16_t *gy, int16_t *gz) {
    uint8_t reg = 0x43;
    uint8_t data[6];  // 6바이트 버퍼 사용
    // 0x43 레지스터부터 6바이트 읽음
    esp_err_t err = i2c_master_write_read_device(I2C_MASTER_NUM, addr, &reg, 1, data, sizeof(data), pdMS_TO_TICKS(100));
    if (err != ESP_OK) return err;
    *gx = ((int16_t)data[0] << 8) | data[1];
    *gy = ((int16_t)data[2] << 8) | data[3];
    *gz = ((int16_t)data[4] << 8) | data[5];
    return ESP_OK;
}

// LED 초기화 함수
void setup_leds(void) {
    for (int i = 0; i < 5; i++) {
        esp_rom_gpio_pad_select_gpio(led_pins[i]);
        gpio_set_direction(led_pins[i], GPIO_MODE_OUTPUT);
        gpio_set_level(led_pins[i], 0);
    }
}

// LED 타이머를 위한 전역 변수 추가
static TimerHandle_t led_timer = NULL;

// LED를 끄는 타이머 콜백 함수
void led_timer_callback(TimerHandle_t xTimer) {
    // 모든 LED를 끔
    for (int i = 0; i < 5; i++) {
        gpio_set_level(led_pins[i], 0);
    }
    ESP_LOGI(TAG, "LEDs turned off after timeout");
}


// LED 제어 함수 수정
void update_leds(double avg_score) {
    int num_leds_on = (int)(avg_score / 20); // 20점당 1개의 LED
    
    // LED 상태 업데이트
    for (int i = 0; i < 5; i++) {
        gpio_set_level(led_pins[i], i < num_leds_on ? 1 : 0);
    }
    
    ESP_LOGI(TAG, "Avg Score: %.3f -> LEDs On: %d", avg_score, num_leds_on);
    
    // 이전 타이머가 있다면 중지하고 삭제
    if (led_timer != NULL) {
        xTimerStop(led_timer, 0);
        xTimerDelete(led_timer, 0);
    }
    
    // 새로운 10초 타이머 생성 및 시작
    led_timer = xTimerCreate("LED_Timer", pdMS_TO_TICKS(10000), pdFALSE, 0, led_timer_callback);
    if (led_timer != NULL) {
        xTimerStart(led_timer, 0);
    }
}


// TCP 서버 태스크: Jetson으로부터 avg_score 수신 및 처리
void score_receiver_task(void *arg) {
    int listen_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
    if (listen_sock < 0) {
        ESP_LOGE(TAG, "Unable to create socket: errno %d", errno);
        vTaskDelete(NULL);
        return;
    }

    struct sockaddr_in server_addr = {
        .sin_family = AF_INET,
        .sin_addr.s_addr = htonl(INADDR_ANY),
        .sin_port = htons(SCORE_LISTEN_PORT)
    };

    if (bind(listen_sock, (struct sockaddr *)&server_addr, sizeof(server_addr)) != 0) {
        ESP_LOGE(TAG, "Socket bind failed: errno %d", errno);
        close(listen_sock);
        vTaskDelete(NULL);
        return;
    }

    if (listen(listen_sock, 1) != 0) {
        ESP_LOGE(TAG, "Socket listen failed: errno %d", errno);
        close(listen_sock);
        vTaskDelete(NULL);
        return;
    }

    ESP_LOGI(TAG, "Listening for score on port %d...", SCORE_LISTEN_PORT);

    while (1) {
        struct sockaddr_in client_addr;
        socklen_t addr_len = sizeof(client_addr);
        int client_sock = accept(listen_sock, (struct sockaddr *)&client_addr, &addr_len);
        
        if (client_sock < 0) {
            ESP_LOGE(TAG, "Unable to accept connection: errno %d", errno);
            continue;
        }

        char rx_buffer[128];
        int len = recv(client_sock, rx_buffer, sizeof(rx_buffer) - 1, 0);
        if (len > 0) {
            rx_buffer[len] = '\0';
            // 문자열을 double로 변환
            double avg_score = atof(rx_buffer);
            update_leds(avg_score);
        }
        close(client_sock);
    }
    close(listen_sock);
}



// 재연결 태스크: 소켓 연결 실패 시 2초마다 재시도
void reconnect_task(void *arg) {
    while (1) {
        if (sock < 0) {
            ESP_LOGI(TAG, "Attempting to reconnect to server...");
            socket_init();
        }
        vTaskDelay(pdMS_TO_TICKS(2000));
    }
}

// 전역: 보완 필터를 위한 이전 각도 저장 변수 (각 센서별)
static double hand_pitch = 0, hand_roll = 0, hand_yaw = 0;
static double arm_pitch = 0, arm_roll = 0, arm_yaw = 0;
// 전역: 마지막 시간 저장 (마이크로초 단위)
static uint64_t last_time = 0;

// 센서 데이터 전송 태스크: 측정 모드 활성화 시에만 MPU6050 데이터를 읽어 TCP 서버로 전송
void sensor_task(void *arg) {
    int16_t ax1, ay1, az1, gx1, gy1, gz1;
    int16_t ax2, ay2, az2, gx2, gy2, gz2;
    
    while (1) {
        if (is_measuring) {
            // 센서 1 (손) 데이터 읽기: 가속도와 자이로
            if (mpu6050_read_accel(MPU6050_ADDR_1, &ax1, &ay1, &az1) != ESP_OK ||
                mpu6050_read_gyro(MPU6050_ADDR_1, &gx1, &gy1, &gz1) != ESP_OK) {
                ESP_LOGE(TAG, "Failed to read sensor 1 data");
            }
            // 센서 2 (팔) 데이터 읽기: 가속도와 자이로
            if (mpu6050_read_accel(MPU6050_ADDR_2, &ax2, &ay2, &az2) != ESP_OK ||
                mpu6050_read_gyro(MPU6050_ADDR_2, &gx2, &gy2, &gz2) != ESP_OK) {
                ESP_LOGE(TAG, "Failed to read sensor 2 data");
            }
            
            // 시간 차 계산 (초 단위)
            uint64_t now = esp_timer_get_time();
            double dt = 0.01;
            if (last_time != 0) {
                dt = (now - last_time) / 1000000.0;
            }
            last_time = now;
            
            // 자이로 값을 deg/s 단위로 변환 (감도: 131 LSB/°/s)
            double gx1_deg = gx1 / 131.0;
            double gy1_deg = gy1 / 131.0;
            double gz1_deg = gz1 / 131.0;
            double gx2_deg = gx2 / 131.0;
            double gy2_deg = gy2 / 131.0;
            double gz2_deg = gz2 / 131.0;
            
            // 가속도 기반 각도 계산 (deg):
            double accel_pitch_hand = atan2(-ax1, sqrt(ay1 * ay1 + az1 * az1)) * 180.0 / M_PI;
            double accel_roll_hand  = atan2(ay1, az1) * 180.0 / M_PI;
            double accel_pitch_arm  = atan2(-ax2, sqrt(ay2 * ay2 + az2 * az2)) * 180.0 / M_PI;
            double accel_roll_arm   = atan2(ay2, az2) * 180.0 / M_PI;
            
            // 보완 필터 적용 (alpha = 0.98)
            double alpha = 0.98;
            hand_pitch = alpha * (hand_pitch + gx1_deg * dt) + (1 - alpha) * accel_pitch_hand;
            hand_roll  = alpha * (hand_roll  + gy1_deg * dt) + (1 - alpha) * accel_roll_hand;
            hand_yaw   = hand_yaw + gz1_deg * dt;  // yaw는 가속도 보정 없이 자이로 누적
            arm_pitch  = alpha * (arm_pitch + gx2_deg * dt) + (1 - alpha) * accel_pitch_arm;
            arm_roll   = alpha * (arm_roll  + gy2_deg * dt) + (1 - alpha) * accel_roll_arm;
            arm_yaw    = arm_yaw + gz2_deg * dt;
            
            // JSON 객체 생성
            cJSON *root = cJSON_CreateObject();
            cJSON_AddNumberToObject(root, "timestamp", now / 1000000.0);
            cJSON *sensors = cJSON_CreateArray();
            cJSON_AddItemToObject(root, "sensors", sensors);
            
            // 센서 1 (hand) 데이터 JSON에 추가
            cJSON *sensor1 = cJSON_CreateObject();
            cJSON_AddStringToObject(sensor1, "id", "hand");
            cJSON_AddNumberToObject(sensor1, "ax", ax1);
            cJSON_AddNumberToObject(sensor1, "ay", ay1);
            cJSON_AddNumberToObject(sensor1, "az", az1);
            cJSON_AddNumberToObject(sensor1, "gx", gx1);
            cJSON_AddNumberToObject(sensor1, "gy", gy1);
            cJSON_AddNumberToObject(sensor1, "gz", gz1);
            cJSON_AddNumberToObject(sensor1, "pitch", hand_pitch);
            cJSON_AddNumberToObject(sensor1, "yaw", hand_yaw);
            cJSON_AddNumberToObject(sensor1, "roll", hand_roll);
            cJSON_AddItemToArray(sensors, sensor1);
            
            // 센서 2 (arm) 데이터 JSON에 추가
            cJSON *sensor2 = cJSON_CreateObject();
            cJSON_AddStringToObject(sensor2, "id", "arm");
            cJSON_AddNumberToObject(sensor2, "ax", ax2);
            cJSON_AddNumberToObject(sensor2, "ay", ay2);
            cJSON_AddNumberToObject(sensor2, "az", az2);
            cJSON_AddNumberToObject(sensor2, "gx", gx2);
            cJSON_AddNumberToObject(sensor2, "gy", gy2);
            cJSON_AddNumberToObject(sensor2, "gz", gz2);
            cJSON_AddNumberToObject(sensor2, "pitch", arm_pitch);
            cJSON_AddNumberToObject(sensor2, "yaw", arm_yaw);
            cJSON_AddNumberToObject(sensor2, "roll", arm_roll);
            cJSON_AddItemToArray(sensors, sensor2);
            
            char *json_str = cJSON_PrintUnformatted(root);
            ESP_LOGI(TAG, "Sending JSON: %s", json_str);
            if (sock >= 0) {
                int ret = send(sock, json_str, strlen(json_str), 0);
                if (ret < 0) {
                    ESP_LOGE(TAG, "Send failed, errno: %d", errno);
                    close(sock);
                    sock = -1;
                }
            }
            free(json_str);
            cJSON_Delete(root);
        }
        vTaskDelay(pdMS_TO_TICKS(100));
    }
}

// 현재 시간을 밀리초 단위로 반환하는 함수
uint64_t get_current_time_ms() {
    return esp_timer_get_time() / 1000;
}

// 터치 이벤트(디지털 입력 기반) 처리 함수 (디바운싱 및 측정 시작 후 2초 블랭킹 적용)
void handle_touch_event() {
    uint64_t current_time = get_current_time_ms();

    // 디바운싱: 마지막 터치 이벤트 이후 DEBOUNCE_TIME_MS보다 짧으면 무시
    if (current_time - last_touch_time < DEBOUNCE_TIME_MS) {
        return;
    }
    last_touch_time = current_time;

    // 만약 측정 중이면, 시작 후 BLANKING_TIME_MS 미만일 때는 정지 동작 무시
    if (is_measuring && (current_time - start_time < BLANKING_TIME_MS)) {
        ESP_LOGI(TAG, "Stop action ignored due to blanking period");
        return;
    }

    // 측정 상태 토글: 터치 이벤트로 시작/정지 전환
    is_measuring = !is_measuring;
    if (is_measuring) {
        start_time = current_time;  // 측정 시작 시각 기록
        ESP_LOGI(TAG, "Measurement started");
    } else {
        ESP_LOGI(TAG, "Measurement stopped");
    }
}

// 외부 터치 센서 초기화: 이제 내장 터치가 아니라 단순 디지털 입력으로 설정
void touch_sensor_init() {
    esp_rom_gpio_pad_select_gpio(TOUCH_SENSOR_PIN);
    // 필요에 따라 풀업 또는 풀다운 모드를 설정 (아래는 풀업 예)
    gpio_set_direction(TOUCH_SENSOR_PIN, GPIO_MODE_INPUT);
    gpio_set_pull_mode(TOUCH_SENSOR_PIN, GPIO_PULLUP_ONLY);
    ESP_LOGI(TAG, "External touch sensor initialized on GPIO27 as digital input");
}

// 터치 센서 태스크: 주기적으로 디지털 입력을 읽어 변화(특히 상승 에지)가 있을 때 handle_touch_event() 호출
void touch_sensor_task(void *arg) {
    int last_state = 0;
    while (1) {
        int current_state = gpio_get_level(TOUCH_SENSOR_PIN);
        // 상승 에지 검사: 이전 상태가 LOW이고 현재가 HIGH이면 터치 이벤트로 간주 (혹은 반대로 센서에 맞게 수정)
        if (last_state == 0 && current_state == 1) {
            handle_touch_event();
            // 이벤트 과다 발생 방지를 위해 잠시 대기
            vTaskDelay(pdMS_TO_TICKS(300));
        }
        last_state = current_state;
        vTaskDelay(pdMS_TO_TICKS(300));
    }
}

void app_main(void) {
    nvs_flash_init();
    wifi_init();

    // Wi‑Fi 연결 및 IP 획득 대기 (최대 약 10초)
    int retry = 0;
    while (!wifi_connected && retry < 20) {
        vTaskDelay(pdMS_TO_TICKS(500));
        retry++;
    }
    if (!wifi_connected) {
        ESP_LOGE(TAG, "Wi‑Fi connection failed!");
        return;
    }

    socket_init();
    xTaskCreate(reconnect_task, "reconnect_task", 4096, NULL, 5, NULL);

    i2c_master_init();
    mpu6050_init(MPU6050_ADDR_1);
    mpu6050_init(MPU6050_ADDR_2);

    // LED 초기화 및 score_receiver_task 생성 추가
    setup_leds();
    xTaskCreate(score_receiver_task, "score_receiver", 4096, NULL, 5, NULL);

    xTaskCreate(sensor_task, "sensor_task", 4096, NULL, 5, NULL);
    touch_sensor_init();
    xTaskCreate(touch_sensor_task, "touch_sensor_task", 2048, NULL, 5, NULL);
}
