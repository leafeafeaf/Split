from using_gpu import using_gpu
from video_preprocess import process_videos
import tensorflow as tf
import os

using_gpu()

# 입력 폴더와 출력 폴더 경로 설정
input_folder = "../oCam/bawling_o"
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

        for idx in range(0,len(skel_dataset), int(length/4)):
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


