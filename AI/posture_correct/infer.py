import numpy as np
import joblib
import sys
import os
import sys
import json

# similarity_measure 폴더 경로를 추가
classfication_bowling_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "classfication_bowling"))
sys.path.append(classfication_bowling_path)

# 이제 train.py를 import 가능
from preprocessing.video_preprocess import release_capture_for_infer
from angle import extract_for_user

def infer_correction(bowling_end):# (skel_list): # 사용자 교정 지시 (영상 -> 타겟 프레임의 스켈레톤 추출 -> 회귀 -> 예측 후 결과 출력)
    print("-----------------------------------회귀 기반 교정 시작-----------------------------------")
    user_data = []
    #user_data = release_capture_for_infer(skel_list, fps=30) # 사용자의 릴리즈 프레임에 대한 스켈레톤
    # print(user_data)
    user_data = bowling_end
    user_arm, user_pelvis, user_foot = extract_for_user(user_data) # 릴리즈 스켈레톤으로 뽑아낸 3개의 각도
    output = []
   # print(" ----- 팔 각도 분석 시작 ----- ")
    output.append(predict_arm(user_foot, user_arm)) # 각 모델의 입력은 독립변수, 종속변수 순 (예측에 사용되는 x값, 실제값)
   # print(" ----- 발 각도 분석 시작 ----- ")
    output.append(predict_foot(user_pelvis, user_foot))    
   # print(" ----- 골반 각도 분석 시작 ----- ")
    output.append(predict_pelvis(user_foot, user_pelvis))
    to_json(output)


def predict_arm(user_data, actual_value): # 1. 독립변수: foot_angle, 종속변수: arm_angle
    # 모델 불러오기
    model, train_mse = joblib.load("C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\posture_correct\\model\\arm_model.pkl")
    
    arm_correction = ""
    # 예측 수행
    predicted_value = model.predict(np.array([[user_data]]))
    print(f"기댓값: {predicted_value}")
    # 오차율 계산
    error = actual_value - predicted_value 

    error_rate = (error / actual_value) * 100

    # 이상치 판단 (MSE 기반)
    threshold = train_mse * 2  # MSE의 2배 이상이면 이상치로 판단
    print(f"threshold: {train_mse}")
    is_outlier = error > threshold
    # 결과 출력
    print(f"Predicted arm_angle: {predicted_value[0]:.4f}")
    print(f"Actual arm_angle: {actual_value}")
    print(f"Error: {error[0]:.4f}")
    print(f"Error Rate: {error_rate[0]:.2f}%")
    print(f"Outlier Detected: {'Yes' if is_outlier else 'No'}")

    if (error > 0): 
        print(f"실제 값이 더 크다, 오차 : {error}")
        arm_correction = f"오차: {error_rate[0]:.2f} %, 팔을 {abs(error[0]):.2f}도 내리세요."
    else : 
        print(f"실제 값이 더 작다, 오차 : {error}")
        arm_correction = f"오차: {error_rate[0]:.2f} %, 팔을 {abs(error[0]):.2f}도 올리세요."
    return arm_correction

def predict_foot(user_data, actual_value): # 2. 독립변수: pelvis_angle, 종속변수: foot_angle
    # 모델 불러오기
    model, train_mse = joblib.load("C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\posture_correct\\model\\foot_model.pkl")
    foot_correction = ""
    # 예측 수행
    predicted_value = model.predict(np.array([[user_data]]))

    # 오차율 계산
    error = abs(actual_value - predicted_value)
    error_rate = (error / actual_value) * 100

    # 이상치 판단 (MSE 기반)
    threshold = train_mse * 2  # MSE의 2배 이상이면 이상치로 판단
    is_outlier = error > threshold

    # 결과 출력
    print(f"Predicted arm_angle: {predicted_value[0]:.4f}")
    print(f"Actual arm_angle: {actual_value}")
    print(f"Error: {error[0]:.4f}")
    print(f"Error Rate: {error_rate[0]:.2f}%")
    print(f"Outlier Detected: {'Yes' if is_outlier else 'No'}")
    if (error > 0): 
        print(f"실제 값이 더 크다, 오차 : {error}")
        foot_correction = f"오차: {error_rate[0]:.2f} %, 발을 {abs(error[0]):.2f}도 높게 뻗으세요."
    else : 
        print(f"실제 값이 더 작다, 오차 : {error}")
        foot_correction = f"오차: {error_rate[0]:.2f} %, 발을 {abs(error[0]):.2f}도 내리세요."
    return foot_correction

def predict_pelvis(user_data, actual_value): # 3. 독립변수: foot_angle, 종속변수: pelvis_angle
    # 모델 불러오기
    model, train_mse = joblib.load("C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\posture_correct\\model\\pelvis_model.pkl")
    pelvis_correction = ""
    # 예측 수행
    predicted_value = model.predict(np.array([[user_data]]))

    # 오차율 계산
    error = abs(actual_value - predicted_value)
    error_rate = (error / actual_value) * 100

    # 이상치 판단 (MSE 기반)
    threshold = train_mse * 2  # MSE의 2배 이상이면 이상치로 판단
    is_outlier = error > threshold

    # 결과 출력
    print(f"Predicted arm_angle: {predicted_value[0]:.4f}")
    print(f"Actual arm_angle: {actual_value}")
    print(f"Error: {error[0]:.4f}")
    print(f"Error Rate: {error_rate[0]:.2f}%")
    print(f"Outlier Detected: {'Yes' if is_outlier else 'No'}")
    if (error > 0): 
        print(f"실제 값이 더 크다, 오차 : {error}")
        pelvis_correction = f"오차: {error_rate[0]:.2f} %, 반대쪽 골반을 {abs(error[0]):.2f}도 넣으세요."
    else : 
        print(f"실제 값이 더 작다, 오차 : {error}")
        pelvis_correction = f"오차: {error_rate[0]:.2f} %, 반대쪽 골반을 {abs(error[0]):.2f}도 빼세요."
    return pelvis_correction

def to_json(instruction):
    output_file="C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\output\\correction.json"
    os.makedirs(os.path.dirname(output_file), exist_ok=True)  # 폴더 없으면 생성
    data = []

    # numpy 데이터 변환 (float32 → float)
    def convert_numpy(obj):
        if isinstance(obj, np.ndarray):  # numpy 배열이면 리스트로 변환
            return obj.tolist()
        elif isinstance(obj, np.float32) or isinstance(obj, np.float64):  # float32, float64를 float으로 변환
            return float(obj)
        elif isinstance(obj, np.int32) or isinstance(obj, np.int64):  # int32, int64를 int로 변환
            return int(obj)
        return obj  # 다른 타입은 그대로 반환

    # 기존 데이터에 새 데이터 추가
    data.extend(instruction)

    # JSON 파일 저장 (변환 적용)
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=4, ensure_ascii=False, default=convert_numpy)

    print(f"✅ Data appended to {output_file}")


# if __name__ == "__main__":
#     if len(sys.argv) != 2:
#         print("Error! : Usage: python your_script.py <video_path>", flush=True)
#         sys.exit(1)

#     video_path = sys.argv[1]
#     infer_correnction(video_path)
    