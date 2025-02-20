#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>

double calculate_avg_score(const std::string& filename) {
    std::ifstream file(filename);
    if (!file.is_open()) {
        std::cerr << "Error opening file: " << filename << std::endl;
        return 0.0;
    }

    std::string line;
    double poseScore = 0.0, elbowAngleScore = 0.0, armStabilityScore = 0.0;
    
    while (std::getline(file, line)) {
        if (line.find("\"poseScore\"") != std::string::npos) {
            size_t pos = line.find(":");
            poseScore = std::stod(line.substr(pos + 1));
        }
        else if (line.find("\"elbowAngleScore\"") != std::string::npos) {
            size_t pos = line.find(":");
            elbowAngleScore = std::stod(line.substr(pos + 1));
        }
        else if (line.find("\"armStabilityScore\"") != std::string::npos) {
            size_t pos = line.find(":");
            armStabilityScore = std::stod(line.substr(pos + 1));
        }
    }
    
    return (poseScore + elbowAngleScore + armStabilityScore) / 3.0;
}

bool send_score_to_esp32(double score, const char* esp32_ip, int port) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        std::cerr << "Socket creation error" << std::endl;
        return false;
    }

    struct sockaddr_in serv_addr;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(port);
    
    if (inet_pton(AF_INET, esp32_ip, &serv_addr.sin_addr) <= 0) {
        std::cerr << "Invalid address" << std::endl;
        return false;
    }

    if (connect(sock, (struct sockaddr*)&serv_addr, sizeof(serv_addr)) < 0) {
        std::cerr << "Connection Failed" << std::endl;
        return false;
    }

    // Convert score to string and send
    std::string score_str = std::to_string(score);
    send(sock, score_str.c_str(), score_str.length(), 0);
    
    close(sock);
    return true;
}

int main() {
    const char* esp32_ip = "192.168.137.103";  // ESP32 IP 주소
    const int port = 6000;                      // ESP32 포트
    
    double avg_score = calculate_avg_score("analysis_result.json");
    if (send_score_to_esp32(avg_score, esp32_ip, port)) {
        std::cout << "Score " << avg_score << " sent successfully" << std::endl;
    }
    
    return 0;
}
