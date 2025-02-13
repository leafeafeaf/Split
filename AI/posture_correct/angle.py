'''
입력으로 받는 데이터 
[[[17,2], [17, 2], .. [17, 2]

(17,2)

KEYPOINT_DICT = {
    'nose': 0,
    'left_eye': 1,
    'right_eye': 2,
    'left_ear': 3,
    'right_ear': 4,
    'left_shoulder': 5,
    'right_shoulder': 6,
    'left_elbow': 7,
    'right_elbow': 8,
    'left_wrist': 9,
    'right_wrist': 10,
    'left_hip': 11,
    'right_hip': 12,
    'left_knee': 13,
    'right_knee': 14,
    'left_ankle': 15,
    'right_ankle': 16
}

'''
import json

def angle_extract():
    with open("C:\\Users\\SSAFY\\code\\PJT\\S12P11B202\\AI\\classfication_bowling\\preprocessing\\release_skel_1.json", "r", encoding="utf-8") as f:
        skel_datas = json.load(f)
    arm_angle = [] 
    pelvis_angle = [] 
    foot_angle = []
    idx = 0

    for skel_data in skel_datas:

        print(f"{idx} : {skel_data[7]}")
        # 각도 저장
        first = cosine(skel_data[7], skel_data[5], skel_data[11]) # 8 6 12 -> 오른쪽 팔
        second = cosine(skel_data[6], skel_data[4], skel_data[10]) # 7 5 11 -> 왼쪽 팔
        third = cosine(skel_data[5], skel_data[11], skel_data[13]) # 6 12 14 -> 오른쪽 골반
        fourth = cosine(skel_data[4], skel_data[10], skel_data[12]) # 5 11 13 -> 왼쪽 골반
        
        print(f"first : {first}")
        print(f"second : {second}")
        print(f"third : {third}")
        print(f"fourth : {fourth}")

        if first > second:
            print("왼손 잡이")
            arm_angle.append(first)
            pelvis_angle.append(third)
            foot_angle.append(fourth)
        else:
            print("오른손 잡이")
            arm_angle.append(second)
            pelvis_angle.append(fourth)
            foot_angle.append(third)
        idx += 1

    # arm_angle_avg = np.average(arm_angle)
    # pelvis_angle_avg = np.average(pelvis_angle)
    # foot_angle_avg = np.average(foot_angle)


    # CSV 저장
    csv_file = "angles_data.csv"
    with open(csv_file, 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(["arm_angle", "pelvis_angle", "foot_angle"])  # 헤더 추가
       # writer.writerow([arm_angle_avg, pelvis_angle_avg, foot_angle_avg])
        for i in range(len(arm_angle)):
            writer.writerow([arm_angle[i], pelvis_angle[i], foot_angle[i]])


import numpy as np
import csv

def cosine(skel_left1, skel_left2, skel_mid1, skel_mid2, skel_right1, skel_right2):
    # 벡터 정의
    vec_a = np.array([skel_left1, skel_left2]) - np.array([skel_mid1, skel_mid2])  # skel_mid -> skel_left
    vec_b = np.array([skel_right1, skel_right2]) - np.array([skel_mid1, skel_mid2])  # skel_mid -> skel_right

    # 벡터의 크기 (norm) 계산
    norm_a = np.linalg.norm(vec_a)
    norm_b = np.linalg.norm(vec_b)

    # 내적 계산
    dot_product = np.dot(vec_a, vec_b)

    # 코사인 값 계산 (예외처리: norm이 0이면 각도 0도 반환)
    if norm_a == 0 or norm_b == 0:
        return 0.0
    
    cos_theta = dot_product / (norm_a * norm_b)

    # arccos로 각도 계산 (라디안 -> 도)
    angle = np.degrees(np.arccos(np.clip(cos_theta, -1.0, 1.0)))

    return angle



# --- 사용자 각도만 추출하는 함수 ---

def extract_for_user(skel_data):

    arm_angle = 0
    pelvis_angle = 0
    foot_angle = 0

    # first = cosine(skel_data[7], skel_data[5], skel_data[11]) # 8 6 12 -> 오른쪽 팔
    # second = cosine(skel_data[6], skel_data[4], skel_data[10]) # 7 5 11 -> 왼쪽 팔
    # third = cosine(skel_data[5], skel_data[11], skel_data[13]) # 6 12 14 -> 오른쪽 골반
    # fourth = cosine(skel_data[4], skel_data[10], skel_data[12]) # 5 11 13 -> 왼쪽 골반
        


    first = cosine(skel_data[14], skel_data[15], skel_data[10], skel_data[11], skel_data[22], skel_data[23]) # 8 6 12 -> 오른쪽 팔
    second = cosine(skel_data[12], skel_data[13], skel_data[8], skel_data[9], skel_data[20], skel_data[21]) # 7 5 11 -> 왼쪽 팔
    third = cosine(skel_data[10], skel_data[11], skel_data[22], skel_data[23], skel_data[26], skel_data[27]) # 6 12 14 -> 오른쪽 골반
    fourth = cosine(skel_data[8], skel_data[9], skel_data[20], skel_data[21], skel_data[24], skel_data[25]) # 5 11 13 -> 왼쪽 골반

    print(f"first : {first}")
    print(f"second : {second}")
    print(f"third : {third}")
    print(f"fourth : {fourth}")

    if first < second:
        print("왼손 잡이")
        arm_angle = first
        pelvis_angle = third
        foot_angle = fourth
    else:
        print("오른손 잡이")
        arm_angle = second
        pelvis_angle = fourth
        foot_angle = third
    
    return arm_angle, pelvis_angle, foot_angle

