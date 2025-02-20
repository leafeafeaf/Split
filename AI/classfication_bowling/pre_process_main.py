import os
from config import device
import tensorflow as tf
from preprocessing.video_preprocess import process_videos


os.environ["TF_GPU_ALLOCATOR"] = "cuda_malloc_async"

print(device)

# 입력 폴더와 출력 폴더 경로 설정
input_folder = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\oCam\\bowlingwallking"
output_folder = "C:\\Users\\SSAFY\\Documents\\output"

# 비디오 처리
with tf.device("/GPU:0"):
    process_videos(input_folder, output_folder, fps=30)
    