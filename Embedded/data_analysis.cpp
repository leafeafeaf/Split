#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <cmath>
#include "json.hpp"   // nlohmann/json 헤더

using ordered_json = nlohmann::ordered_json;
using json = nlohmann::json;

struct SensorData {
    double time;
    double ax;
    double ay;
    double az;
    double gx;    // 추가: 자이로 x축
    double gy;    // 추가: 자이로 y축
    double gz;    // 추가: 자이로 z축
    double pitch;
    double yaw;
    double roll;
};

// 이동 평균 필터 클래스
class MovingAverageFilter {
private:
    std::vector<double> values;
    size_t windowSize;
    
public:
    MovingAverageFilter(size_t size) : windowSize(size) {}
    
    double filter(double newValue) {
        values.push_back(newValue);
        if (values.size() > windowSize) {
            values.erase(values.begin());
        }
        
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.size();
    }
};
// 센서 데이터 필터링 함수
void filterSensorData(std::vector<SensorData>& data) {
    MovingAverageFilter pitchFilter(5);  // 5개 샘플 윈도우
    MovingAverageFilter yawFilter(5);
    
    for (auto& sample : data) {
        sample.pitch = pitchFilter.filter(sample.pitch);
        sample.yaw = yawFilter.filter(sample.yaw);
    }
}

double calculateMaxHandSpeed(const std::vector<SensorData>& handData, double startTime, double endTime) {
    double vx = 0.0, vy = 0.0, vz = 0.0, maxSpeed = 0.0;
    double conversionFactor = 9.81 / 16384.0;  // 원시값을 m/s^2로 변환
    double gyroFactor = 1.0 / 131.0;          // 자이로 변환 계수 (deg/s)
    for (size_t i = 1; i < handData.size(); i++) {
        if (handData[i].time < startTime || handData[i].time > endTime)
            continue;
        double dt = handData[i].time - handData[i-1].time;
        double eff_ax = handData[i].ax * conversionFactor;
        double eff_ay = handData[i].ay * conversionFactor;
        double eff_az = handData[i].az * conversionFactor - 9.81; // 중력 보정
        
        double wx = handData[i].gx * gyroFactor;
        double wy = handData[i].gy * gyroFactor;
        double wz = handData[i].gz * gyroFactor;
        
         // 선형 가속도와 각속도를 결합하여 속도 계산
        vx += (eff_ax + (wy * vz - wz * vy)) * dt;
        vy += (eff_ay + (wz * vx - wx * vz)) * dt;
        vz += (eff_az + (wx * vy - wy * vx)) * dt;
        
        double speed = std::sqrt(vx * vx + vy * vy + vz * vz);
        if (speed > maxSpeed)
            maxSpeed = speed;
    }
    return maxSpeed;
}

int countElbowBendingEvents(const std::vector<SensorData>& handData,
                           const std::vector<SensorData>& armData,
                           double startTime, double endTime,
                           double threshold = 5.0) {  // 임계값 낮춤
    int events = 0;
    double prevDiffPitch = 0.0;
    size_t n = std::min(handData.size(), armData.size());
    
    for (size_t i = 1; i < n; i++) {
        double t = handData[i].time;
        if (t < startTime || t > endTime)
            continue;
            
        // 현재 pitch 차이 계산
        double currentDiffPitch = std::fabs(handData[i].pitch - armData[i].pitch);
        
        // 이전 샘플과의 변화량 계산
        double pitchChange = std::fabs(currentDiffPitch - prevDiffPitch);
        
        // 임계값 초과 또는 급격한 변화 감지
        if (currentDiffPitch > threshold || pitchChange > threshold/2) {
            events++;
            i += 2;  // 연속된 이벤트 감지 방지
        }
        
        prevDiffPitch = currentDiffPitch;
    }
    return events;
}

int countStraightDeviation(const std::vector<SensorData>& handData,
                          const std::vector<SensorData>& armData,
                          double startTime, double endTime,
                          double threshold = 10.0) {  // 임계값 높임
    int count = 0;
    double prevDiff = 0.0;
    size_t n = std::min(handData.size(), armData.size());
    
    for (size_t i = 1; i < n; i++) {
        double t = handData[i].time;
        if (t < startTime || t > endTime)
            continue;
            
        // 2D 유클리드 거리 계산
        double diffPitch = std::fabs(handData[i].pitch - armData[i].pitch);
        double diffYaw = std::fabs(handData[i].yaw - armData[i].yaw);
        double currentDiff = std::sqrt(diffPitch * diffPitch + diffYaw * diffYaw);
        
        // 변화량 계산
        double diffChange = std::fabs(currentDiff - prevDiff);
        
        // 임계값 초과 또는 급격한 변화 감지
        if (currentDiff > threshold || diffChange > threshold/2) {
            count++;
            i += 2;  // 연속된 이벤트 감지 방지
        }
        
        prevDiff = currentDiff;
    }
    return count;
}


double clamp(double val, double min_val, double max_val) {
    if (val < min_val) return min_val;
    if (val > max_val) return max_val;
    return val;
}

double roundTo3Decimal(double val) {
    return std::round(val * 1000.0) / 1000.0;
}

int main(int argc, char* argv[]) {
    if (argc != 3) {
        std::cerr << "Usage: data_analysis <input_json> <output_json>" << std::endl;
        return 1;
    }
    
    // 1. data.json 파일 읽기 (argv[1])
    std::ifstream ifs(argv[1]);
    if (!ifs) {
        std::cerr << "Error opening input file: " << argv[1] << std::endl;
        return 1;
    }
    json j;
    try {
        ifs >> j;
    } catch (json::parse_error& e) {
        std::cerr << "JSON parse error: " << e.what() << std::endl;
        return 1;
    }
    if (!j.is_object() || !j.contains("data") || !j["data"].is_array()) {
        std::cerr << "Input JSON format error: top-level object must contain 'data' array" << std::endl;
        return 1;
    }
    
    std::vector<SensorData> handData;
    std::vector<SensorData> armData;
    
    // 2. data.json 내 각 요소 파싱 (각 요소는 문자열로 저장된 JSON 데이터)
    for (auto& item : j["data"]) {
        json sample;
        try {
            sample = json::parse(item.get<std::string>());
        } catch (json::parse_error& e) {
            std::cerr << "Parse error: " << e.what() << std::endl;
            continue;
        }
        double t = sample["timestamp"].get<double>();
       // data.json 내 각 요소 파싱 부분에서
		for (auto& sensor : sample["sensors"]) {
		    std::string id = sensor["id"].get<std::string>();
		    SensorData data;
		    data.time = t;
		    data.ax = sensor.value("ax", 0.0);
		    data.ay = sensor.value("ay", 0.0);
		    data.az = sensor.value("az", 0.0);
		    data.gx = sensor.value("gx", 0.0);  // 추가: 자이로 데이터 읽기
		    data.gy = sensor.value("gy", 0.0);  // 추가: 자이로 데이터 읽기
		    data.gz = sensor.value("gz", 0.0);  // 추가: 자이로 데이터 읽기
		    data.pitch = sensor.value("pitch", 0.0);
		    data.yaw = sensor.value("yaw", 0.0);
		    data.roll = sensor.value("roll", 0.0);
		    if (id == "hand")
		        handData.push_back(data);
		    else if (id == "arm")
		        armData.push_back(data);
		}

    }
    
    if (handData.empty()){
        std::cerr << "No hand data available." << std::endl;
        return 1;
    }
    // 필터링 적용
    filterSensorData(handData);
    filterSensorData(armData);
        
    
    double startTime = 0.0;
    double endTime = handData.back().time;
    
    double maxSpeed = calculateMaxHandSpeed(handData, startTime, endTime);
    int deviationCount = countStraightDeviation(handData, armData, startTime, endTime);
    int bendingEvents = countElbowBendingEvents(handData, armData, startTime, endTime);
    
    size_t totalSamples = handData.size();
    double ratioElbow = bendingEvents / static_cast<double>(totalSamples);
    double ratioStability = deviationCount / static_cast<double>(totalSamples);
    
    // 개선된 계산 로직: 스케일 팩터를 1.2로 적용하여 이벤트 비율을 반영
    const double scaleElbow = 1.2;
    const double scaleStability = 1.2;
    
    double rawElbowScore = 100.0 * (1.0 - (ratioElbow * scaleElbow));
    double elbowAngleScore = clamp(rawElbowScore, 0.0, 100.0);
    
    double rawStabilityScore = 100.0 * (1.0 - (ratioStability * scaleStability));
    double armStabilityScore = clamp(rawStabilityScore, 0.0, 100.0);
    
    // 속도 점수화: 최대 가능 속도 10 m/s 기준으로 0~100 점으로 매핑
    const double maxPossibleSpeed = 10.0;
    double rawSpeedScore = (maxSpeed / maxPossibleSpeed) * 100.0;
    double correctionFactor = 100.0; // 실험적으로 조정할 보정 계수.
    double speedScore = clamp(rawSpeedScore * correctionFactor, 0.0, 100.0);
    
    // 최종 점수들을 소수점 셋째 자리에서 반올림
    elbowAngleScore = roundTo3Decimal(elbowAngleScore);
    armStabilityScore = roundTo3Decimal(armStabilityScore);
    speedScore = roundTo3Decimal(speedScore);
    
    // 3. perfect_correction.json에서 feedback 값 읽기
    json jCorrection;
    {
        std::ifstream ifsCorrection("perfect_correction.json");
        if (!ifsCorrection) {
            std::cerr << "Error opening perfect_correction.json file." << std::endl;
            return 1;
        }
        try {
            ifsCorrection >> jCorrection;
        } catch (json::parse_error& e) {
            std::cerr << "perfect_correction.json parse error: " << e.what() << std::endl;
            return 1;
        }
    }
    std::string feedback;
    if (jCorrection.is_object() && jCorrection.contains("feedback"))
        feedback = jCorrection["feedback"].get<std::string>();
    else
        feedback = jCorrection.dump();
    
    // 4. similarity_Score.json에서 poseScore 값 읽기
    json jSimilarity;
    {
        std::ifstream ifsSimilarity("similarity_Score.json");
        if (!ifsSimilarity) {
            std::cerr << "Error opening similarity_Score.json file." << std::endl;
            return 1;
        }
        std::string simContent((std::istreambuf_iterator<char>(ifsSimilarity)),
                                   std::istreambuf_iterator<char>());
        size_t startPos = simContent.find_first_of('{');
        if (startPos == std::string::npos) {
            std::cerr << "Could not find '{' in similarity_Score.json." << std::endl;
            return 1;
        }
        int braceCount = 0;
        size_t endPos = startPos;
        bool jsonFound = false;
        for (size_t i = startPos; i < simContent.size(); i++) {
            char c = simContent[i];
            if (c == '{')
                braceCount++;
            else if (c == '}')
                braceCount--;
            if (braceCount == 0) {
                endPos = i;
                jsonFound = true;
                break;
            }
        }
        if (!jsonFound) {
            std::cerr << "No valid JSON object found in similarity_Score.json." << std::endl;
            return 1;
        }
        std::string simJsonStr = simContent.substr(startPos, endPos - startPos + 1);
        try {
            jSimilarity = json::parse(simJsonStr);
        } catch (json::parse_error& e) {
            std::cerr << "similarity_Score.json parse error: " << e.what() << std::endl;
            return 1;
        }
    }
    double poseScore = 0.0;
    if (jSimilarity.is_number())
        poseScore = jSimilarity.get<double>();
    else if (jSimilarity.is_object()) {
        if (jSimilarity.contains("poseScore"))
            poseScore = jSimilarity["poseScore"].get<double>();
        else if (jSimilarity.contains("score"))
            poseScore = jSimilarity["score"].get<double>();
    }
    
    // 5. 최종 결과 JSON 작성 (ordered_json 사용)
    ordered_json result;
    result["isSkip"] = false;
    result["feedback"] = feedback;
    result["poseScore"] = poseScore;
    result["elbowAngleScore"] = elbowAngleScore;
    result["armStabilityScore"] = armStabilityScore;
    result["speed"] = speedScore;  // speedScore가 0~100 사이의 반올림된 값
    
    std::cout << "Analysis Result:\n" << result.dump(4) << std::endl;
    
    // 6. 결과 JSON 파일 저장 (argv[2] 경로 사용)
    std::ofstream ofs(argv[2], std::ios::out | std::ios::trunc);
    if (!ofs.is_open()) {
        std::cerr << "Error opening output file: " << argv[2] << std::endl;
        return 1;
    }
    ofs << result.dump(4);
    ofs.close();
    std::cout << "Results saved to " << argv[2] << std::endl;
    
    return 0;
}
