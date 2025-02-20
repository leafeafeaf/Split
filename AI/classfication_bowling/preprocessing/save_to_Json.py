import json
import os

import json
import numpy as np
import os

def save_to_json(dataset, output_file="C:\\Users\\SSAFY\\code\\PJT\\S12P11B202\\AI\\classfication_bowling\\preprocessing\\bowling_walking_skeleton_data.json"):
    os.makedirs(os.path.dirname(output_file), exist_ok=True)  # 폴더 없으면 생성

    # 기존 JSON 파일 불러오기 (파일이 있으면 로드, 없으면 빈 리스트)
    if os.path.exists(output_file):
        with open(output_file, "r", encoding="utf-8") as f:
            try:
                existing_data = json.load(f)
            except json.JSONDecodeError:
                existing_data = []
    else:
        existing_data = []

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
    existing_data.extend(dataset)

    # JSON 파일 저장 (변환 적용)
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(existing_data, f, indent=4, default=convert_numpy)

    print(f"✅ Data appended to {output_file}")


def save_skel_to_json(skel_output,skel_output_folder,skel_frame_idx,):
    skel_filename = os.path.join(skel_output_folder, f'skel_data_{skel_frame_idx:03d}.json')
    os.makedirs(os.path.dirname(skel_filename), exist_ok=True)  # 폴더 없으면 생성

    # numpy 데이터 변환 (float32 → float)
    def convert_numpy(obj):
        if isinstance(obj, np.ndarray):  # numpy 배열이면 리스트로 변환
            return obj.tolist()
        elif isinstance(obj, np.float32) or isinstance(obj, np.float64):  # float32, float64를 float으로 변환
            return float(obj)
        elif isinstance(obj, np.int32) or isinstance(obj, np.int64):  # int32, int64를 int로 변환
            return int(obj)
        return obj  # 다른 타입은 그대로 반환
    
    # JSON 파일 저장 (변환 적용)
    with open(skel_filename, "w", encoding="utf-8") as f:
        json.dump(skel_output, f, indent=4, default=convert_numpy)

    print(f"✅ Data appended to {skel_filename}")