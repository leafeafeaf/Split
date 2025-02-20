#include <iostream>
#include <vector>
#include <string>
#include <fstream>
#include <cstring>
#include <cstdlib>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/time.h>
#include "json.hpp"   // nlohmann/json 헤더 파일

using json = nlohmann::json;

#define PORT 5000
#define BUFF_SIZE 1024
#define STOP_SIGNAL_FILE "stop_signal.txt"

int main(int argc, char* argv[]) {
    if (argc > 1) {
        std::string cmd(argv[1]);
        if (cmd == "start") {
            std::cout << "Start command received. Starting data collection." << std::endl;
        } else {
            std::cerr << "알 수 없는 명령: " << cmd << std::endl;
            return 1;
        }
    }

    int server_fd, client_socket;
    struct sockaddr_in address;
    int addrlen = sizeof(address);
    char buffer[BUFF_SIZE];
    int opt = 1;

    // 서버 소켓 생성
    server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0) {
        std::cerr << "Socket creation failed, errno: " << errno << std::endl;
        exit(EXIT_FAILURE);
    }

    // 소켓 옵션 설정 (주소 재사용)
    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt)) < 0) {
        std::cerr << "setsockopt failed, errno: " << errno << std::endl;
        exit(EXIT_FAILURE);
    }

    // 주소 정보 설정: 모든 인터페이스, 지정 포트
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    // 주소 바인딩
    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
        std::cerr << "Bind failed, errno: " << errno << std::endl;
        exit(EXIT_FAILURE);
    }

    // 연결 수신 대기 (백로그 크기를 10으로 설정)
    if (listen(server_fd, 10) < 0) {
        std::cerr << "Listen failed, errno: " << errno << std::endl;
        exit(EXIT_FAILURE);
    }

    std::cout << "Server listening on port " << PORT << std::endl;

    // 클라이언트 연결 수락
    client_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t*)&addrlen);
    if (client_socket < 0) {
        std::cerr << "Accept failed, errno: " << errno << std::endl;
        exit(EXIT_FAILURE);
    }
    std::cout << "Connection accepted from " << inet_ntoa(address.sin_addr) << std::endl;

    // 수신에 타임아웃 설정 (5초)
    struct timeval tv;
    tv.tv_sec = 5;
    tv.tv_usec = 0;
    if (setsockopt(client_socket, SOL_SOCKET, SO_RCVTIMEO, (const char*)&tv, sizeof(tv)) < 0) {
        std::cerr << "setsockopt for timeout failed, errno: " << errno << std::endl;
    }

    // 수신한 JSON 문자열들을 저장할 벡터
    std::vector<std::string> received_msgs;

    // 데이터 수신 루프
    while (true) {
        // Stop 신호 파일 존재 여부 확인
        if (access(STOP_SIGNAL_FILE, F_OK) == 0) {
            std::cout << "Stop signal detected. Exiting data collection loop." << std::endl;
            break; // 루프 종료
        }

        ssize_t valread = recv(client_socket, buffer, BUFF_SIZE - 1, 0);
        if (valread <= 0) {
            std::cout << "No data received for 5 seconds." << std::endl;
            continue; // 타임아웃 발생 시 루프 계속 진행
        }
        buffer[valread] = '\0';  // 널 종료
        std::string msg(buffer);
        std::cout << "Received: " << msg << std::endl;
        received_msgs.push_back(msg);
    }

    // 모든 메시지를 JSON 배열에 저장
    json j;
    j["data"] = received_msgs;

    // JSON 파일로 저장 (4칸 들여쓰기)
    std::ofstream ofs("data.json");
    if (ofs.is_open()) {
        ofs << j.dump(4);
        ofs.close();
        std::cout << "Saved received data to data.json" << std::endl;
    } else {
        std::cerr << "Could not open file to save JSON data" << std::endl;
    }

    close(client_socket);
    close(server_fd);

    // Stop 신호 파일 삭제
    remove(STOP_SIGNAL_FILE);

    return 0;
}
