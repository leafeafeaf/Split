import numpy as np
from preprocessing.save_to_Json import save_to_json

def labeling(video_name,video_skel):
    dataset = [] # 20개씩 묶인 시퀀스 데이터! 의 리스트 
    length = 10 # 시퀀스 데이터를 20개씩 묶기 위한 변수
    interval = (int)(length / 2)

    # 동영상 이름에 'o' 포함이면 label 1 / 'x'이면 label 0
    # for video_name, video_skel in zip(video_name_list, skel_dataset):
    label = 1 if 'walking' in video_name else 0
    last_idx = -1  # 마지막으로 추가된 인덱스 추적
    if len(video_skel) >= length:
        for idx in range(0, len(video_skel) - length + 1, interval):
            seq_list = video_skel[idx: idx + length]    
            dataset.append({'key': label, 'value': seq_list})
            last_idx = idx  # 마지막 추가된 인덱스 업데이트
            print(f"현재 seq list 크기 : {len(seq_list)}")

            # 마지막 `length` 프레임 추가 (이미 추가되지 않았을 경우)
        if last_idx != len(video_skel) - length:
            dataset.append({'key': label, 'value': video_skel[-length:]})
            print(f"마지막 seq list 크기 : {len(seq_list)}")

        print(f"dataset 크기 : {len(dataset)}")
        save_to_json(dataset)