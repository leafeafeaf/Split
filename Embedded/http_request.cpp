#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <curl/curl.h>

// 응답 저장용 콜백 함수
size_t WriteCallback(void* contents, size_t size, size_t nmemb, void* userp) {
    ((std::string*)userp)->append((char*)contents, size * nmemb);
    return size * nmemb;
}

int main(int argc, char* argv[]) {
    if (argc != 2) {
        std::cerr << "사용법: " << argv[0] << " <json_file>" << std::endl;
        return 1;
    }

    // 입력 JSON 파일 열기
    std::ifstream ifs(argv[1]);
    if (!ifs) {
        std::cerr << "파일 열기 실패: " << argv[1] << std::endl;
        return 1;
    }

    // JSON 파일 내용 읽기
    std::stringstream buffer;
    buffer << ifs.rdbuf();
    std::string jsonData = buffer.str();

    // libcurl 초기화
    CURL *curl;
    CURLcode res;
    std::string response_string;
    
    curl_global_init(CURL_GLOBAL_DEFAULT);
    curl = curl_easy_init();
    if (curl) {
        // 전송할 서버 URL을 설정
        curl_easy_setopt(curl, CURLOPT_URL, "https://i12b202.p.ssafy.io/api/device/1/frame");

        // POST 방식 전송 설정
        curl_easy_setopt(curl, CURLOPT_POST, 1L);

        // POST 데이터로 JSON 문자열 지정
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonData.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, jsonData.size());

        // 응답 받기 위한 콜백 설정
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response_string);

        // HTTP 헤더 설정 - JSON 데이터임을 표시
        struct curl_slist *headers = NULL;
        headers = curl_slist_append(headers, "Content-Type: application/json");
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

        // HTTP POST 요청 실행
        res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            std::cerr << "HTTP POST 요청 실패: " << curl_easy_strerror(res) << std::endl;
        } else {
            std::cout << "HTTP POST 요청 성공!" << std::endl;
            std::cout << "서버 응답: " << response_string << std::endl;
            
            // num 값 파싱 및 저장
            size_t pos = response_string.find("\"num\":");
            if (pos != std::string::npos) {
                pos += 6; // "num": 다음 위치로
                std::string num = response_string.substr(pos);
                num = num.substr(0, num.find_first_of(",}"));
                
                // num.txt 파일에 저장
                std::ofstream numFile("num.txt");
                if (numFile.is_open()) {
                    numFile << num;
                    numFile.close();
                    std::cout << "num 값 " << num << " 저장됨" << std::endl;
                }
            }
        }

        // 할당된 헤더와 cURL 핸들러 정리
        curl_slist_free_all(headers);
        curl_easy_cleanup(curl);
    }

    curl_global_cleanup();
    return 0;
}
