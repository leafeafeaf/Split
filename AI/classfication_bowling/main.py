from using_gpu import *
from preprocessing.video_preprocess import process_videos
import tensorflow as tf
import os
from torch.utils.data import random_split
from torch.utils.data import DataLoader
from training.input import InputData
from training.train import epoch_not_finished,epoch
from training.visualization import init_model,init_epoch,init_log,record_valid_log,print_log,record_train_log
from config import device
#using_gpu_tf()
#device = using_gpu_torch()
print(device)

# 입력 폴더와 출력 폴더 경로 설정
input_folder = "C:\\Users\\SSAFY\\code\\PJT\\S12P11B202\\AI\\oCam\\bowling_o"
output_folder = "C:\\Users\\SSAFY\\Documents\\output"

# 비디오 처리
with tf.device("/gpu:0"):
    skel_dataset = process_videos(input_folder, output_folder, fps=30)
    
    video_name_list = os.listdir(input_folder)
    print(len(video_name_list))

    dataset=[]
    length = 20
    interval = 1

    # 동영상 이름에 'o' 포함이면 label 1 / 'x'이면 label 0
    for video_name in video_name_list:
        if 'o' in video_name:
            label = 1
        else:
            label = 0

        for idx in (0,len(skel_dataset), int(length/4)):
            seq_list = skel_dataset[idx : idx+length]
            if len(seq_list) == length:
                dataset.append({'key': label, 'value': seq_list})
            
        if len(skel_dataset) % (length // 4) != 0:
            last_seq = skel_dataset[-length:]
            dataset.append({'key': label, 'value': last_seq})

    print(dataset[0])
    print(dataset[1])

    print(len(dataset))
    print(dataset[0]['value'][15])
    print(len(dataset[0]['value']))

    print(len(dataset[0]['value'][19]))
    # print(len(dataset[13]['value']))


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

    # Training 초기화화

    init_model()
    init_epoch()
    init_log()

    # Training Iteration
    import time

    while epoch_not_finished():

        start_time = time.time()
        tloss, tacc = epoch(train_loader, mode='train')
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

