import os
from config import device
import tensorflow as tf
from preprocessing.video_preprocess import process_videos


os.environ["TF_GPU_ALLOCATOR"] = "cuda_malloc_async"

#using_gpu_tf()
#device = using_gpu_torch()
print(device)

# 입력 폴더와 출력 폴더 경로 설정
input_folder = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\oCam\\bowlingwallking"
output_folder = "C:\\Users\\SSAFY\\Documents\\output"

# # 모든 하위 폴더 포함하여 처리
# all_videos = []
# for root, _, files in os.walk(input_folder):  # 하위 폴더까지 탐색
#     for file in files:
#         if file.endswith(('.mp4')):  # 원하는 확장자 필터링
#             all_videos.append(os.path.join(root, file))

# 비디오 처리
with tf.device("/GPU:0"):
    process_videos(input_folder, output_folder, fps=30)
    # skel_dataset = process_videos(input_folder, output_folder, fps=30)

    # print("video_process output: ")
    # print(len(skel_dataset))
    # video_name_list = os.listdir(input_folder)
    # print(len(video_name_list))