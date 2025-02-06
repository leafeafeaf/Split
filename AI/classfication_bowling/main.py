from using_gpu import *
from preprocessing.video_preprocess import process_videos
import tensorflow as tf
import numpy as np
import os

from torch.utils.data import random_split
from torch.utils.data import DataLoader
import config
from training.input import InputData
from training.train import epoch_not_finished,epoch
from training.visualization import init_model,init_epoch,init_log,record_valid_log,print_log,record_train_log
from config import device
from inference import infer

os.environ["TF_GPU_ALLOCATOR"] = "cuda_malloc_async"

#using_gpu_tf()
#device = using_gpu_torch()
print(device)

# 입력 폴더와 출력 폴더 경로 설정
input_folder = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\oCam\\test"
output_folder = "C:\\Users\\SSAFY\\Documents\\output"

# # 모든 하위 폴더 포함하여 처리
# all_videos = []
# for root, _, files in os.walk(input_folder):  # 하위 폴더까지 탐색
#     for file in files:
#         if file.endswith(('.mp4')):  # 원하는 확장자 필터링
#             all_videos.append(os.path.join(root, file))

# 비디오 처리
# with tf.device("/gpu:0"):
    # process_videos(input_folder, output_folder, fps=30)
    # skel_dataset = process_videos(input_folder, output_folder, fps=30)

    # print("video_process output: ")
    # print(len(skel_dataset))
    # video_name_list = os.listdir(input_folder)
    # print(len(video_name_list))

    
with tf.device("/gpu:0"):
## dataset은 csv에서 가져오기
    import json

# 저장된 JSON 파일을 읽어오기
    with open("C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\classfication_bowling\\preprocessing\\labeled_skeleton_data.json", "r", encoding="utf-8") as f:
        dataset = json.load(f)
    # --- train, validation, test 비율율 할당 ---

    split_ratio = [0.8, 0.1, 0.1]
    train_len = int(len(dataset) * split_ratio[0])
    val_len = int(len(dataset) * split_ratio[1])
    test_len = len(dataset)-train_len-val_len

    train_dataset = InputData(dataset)
    train_data, valid_data, test_data = random_split(train_dataset, [train_len, val_len, test_len])

    train_loader = DataLoader(train_data, batch_size=8) # 8개씩 샘플 입력
    val_loader = DataLoader(valid_data, batch_size=8)
    test_loader = DataLoader(test_data, batch_size=8)

    # Training 초기화

    init_model()
    init_epoch() # epoch_cnt = 0 초기화
    print(config.epoch_cnt)
    init_log()

    # Training Iteration
    import time

    while epoch_not_finished(): # 여기서 epoch_cnt를 전역으로 못 읽어옴
        print("현재 epoch: ")
        print(config.epoch_cnt)
        start_time = time.time()
        tloss, tacc = epoch(train_loader, mode='train') # epoch_cnt += 1
        end_time = time.time()
        time_taken = end_time - start_time
        record_train_log(tloss, tacc, time_taken)
        
        with torch.no_grad(): # 역전파 학습을 하지 않도록(더 이상 gradient를 트래킹하지 않도록)
            vloss, vacc = epoch(val_loader, mode='val')
            record_valid_log(vloss, vacc)
            
    print_log()

    print('※n Training completed!')

    with torch.no_grad():
        test_loss, test_acc = epoch(test_loader, mode = 'test')
        test_acc = round(test_acc, 4)
        test_loss = round(test_loss, 4)
        print('test 정확도 : {}' .format(test_acc))
        print('test Loss : {}' .format(test_loss))

    infer()
