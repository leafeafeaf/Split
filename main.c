
#include <stdio.h>
#include <string.h>
#include "lwip/sockets.h"
#include "lwip/netdb.h"
#include <unistd.h>

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_log.h"
#include "esp_system.h"
#include "nvs_flash.h"
#include "esp_wifi.h"
#include "esp_event.h"
#include "esp_netif.h"

// WiFi 설정
#define WIFI_SSID    "DESKTOP-PHKCE21 0954"         // 자신의 WiFi SSID로 변경
#define WIFI_PASS    "P54446k]"     // 자신의 WiFi 비밀번호로 변경

// 서버 설정 (Jetson Orin Nano의 IP와 포트)
#define SERVER_IP    "192.168.137.226"     // Jetson 보드의 IP 주소로 변경
#define SERVER_PORT  5000                // 사용하는 포트 번호

static const char *TAG = "SOCKET_CLIENT";
static int sock = -1;

// WiFi 이벤트 핸들러
static void wifi_event_handler(void* arg, esp_event_base_t event_base,
                               int32_t event_id, void* event_data) {
    if (event_id == WIFI_EVENT_STA_START) {
        esp_wifi_connect();
    } else if (event_id == WIFI_EVENT_STA_DISCONNECTED) {
        esp_wifi_connect();
        ESP_LOGI(TAG, "Retry to connect to the AP");
    } else if (event_id == IP_EVENT_STA_GOT_IP) {
        // 연결되면 IP를 받을 수 있음.
        ip_event_got_ip_t* event = (ip_event_got_ip_t*) event_data;
        ESP_LOGI(TAG, "Got IP:" IPSTR, IP2STR(&event->ip_info.ip));
    }
}

// WiFi 초기화 함수
void wifi_init(void) {
    ESP_ERROR_CHECK(nvs_flash_init());
    ESP_ERROR_CHECK(esp_netif_init());
    ESP_ERROR_CHECK(esp_event_loop_create_default());
    
    // 기본 WiFi STA 인터페이스 생성
    esp_netif_create_default_wifi_sta();
    
    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&cfg));
    
    ESP_ERROR_CHECK(esp_event_handler_instance_register(WIFI_EVENT,
                        ESP_EVENT_ANY_ID, &wifi_event_handler, NULL, NULL));
    ESP_ERROR_CHECK(esp_event_handler_instance_register(IP_EVENT,
                        IP_EVENT_STA_GOT_IP, &wifi_event_handler, NULL, NULL));
    
    wifi_config_t wifi_config = {
        .sta = {
            .ssid = WIFI_SSID,
            .password = WIFI_PASS,
            .threshold.authmode = WIFI_AUTH_WPA2_PSK,
        },
    };
    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
    ESP_ERROR_CHECK(esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config));
    ESP_ERROR_CHECK(esp_wifi_start());
    ESP_LOGI(TAG, "WiFi initialization finished.");
    
    // 연결 안정성을 위한 잠시 대기
    vTaskDelay(3000 / portTICK_PERIOD_MS);
}

// 서버로 소켓 연결 함수
void connect_to_server(void) {
    struct sockaddr_in server_addr;
    sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        ESP_LOGE(TAG, "Failed to create socket");
        return;
    }
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(SERVER_PORT);
    inet_pton(AF_INET, SERVER_IP, &server_addr.sin_addr);

    if (connect(sock, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        ESP_LOGE(TAG, "Failed to connect to server");
        close(sock);
        return;
    }
    ESP_LOGI(TAG, "Connected to server");
}

// 데이터 전송 함수
void send_data(const char *msg) {
    if (sock > 0) {
        int ret = send(sock, msg, strlen(msg), 0);
        if (ret < 0) {
            ESP_LOGE(TAG, "Send failed");
        } else {
            ESP_LOGI(TAG, "Sent: %s", msg);
        }
    }
}

// 테스트 태스크: 주기적으로 "TEST START"와 "TEST STOP" 메시지를 전송
void test_message_task(void *arg) {
    while (1) {
        send_data("TEST START");
        vTaskDelay(2000 / portTICK_PERIOD_MS);  // 2초 대기
        send_data("TEST STOP");
        vTaskDelay(5000 / portTICK_PERIOD_MS);  // 5초 대기 후 다시 반복
    }
}

// 메인 함수
void app_main(void) {
    wifi_init();              // WiFi 초기화
    connect_to_server();      // 서버에 연결
    // 테스트 메시지 전송 태스크 생성
    xTaskCreate(test_message_task, "test_message_task", 4096, NULL, 5, NULL);
}