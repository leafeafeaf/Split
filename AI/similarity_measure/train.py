import tensorflow as tf
from tensorflow.keras.models import Model
from tensorflow.keras.layers import (Input, Conv2D, MaxPooling2D, LSTM, Dense, 
                                   TimeDistributed, Flatten, Concatenate)
import numpy as np
import json
import os
from PIL import Image

def create_hybrid_model(
    seq_length=5,
    img_height=128,
    img_width=128,
    channels=3,
    n_keypoints=17):
    
    # 1. 이미지 입력 스트림
    img_input = Input(shape=(seq_length, img_height, img_width, channels))
    
    # CNN 부분 (TimeDistributed를 사용하여 각 프레임에 동일한 CNN 적용)
    cnn = TimeDistributed(Conv2D(16, (3, 3), activation='relu'))(img_input)
    cnn = TimeDistributed(MaxPooling2D((2, 2)))(cnn)
    cnn = TimeDistributed(Conv2D(32, (3, 3), activation='relu'))(cnn)
    cnn = TimeDistributed(MaxPooling2D((2, 2)))(cnn)
    cnn = TimeDistributed(Flatten())(cnn)
    
    # 2. 키포인트 입력 스트림
    keypoint_input = Input(shape=(seq_length, n_keypoints * 2))
    
    # 두 스트림 결합
    combined = Concatenate(axis=2)([cnn, keypoint_input])
    
    # LSTM 레이어들
    lstm = LSTM(32, return_sequences=True)(combined)
    lstm = LSTM(16)(lstm)
    
    # 출력 레이어 (다음 프레임의 키포인트 예측)
    output = Dense(n_keypoints * 2)(lstm)
    
    # 모델 생성
    model = Model(inputs=[img_input, keypoint_input], outputs=output)
    model.summary()
    return model

# 데이터 배치 생성 함수
def create_training_batch(images, keypoints, seq_length):
    """
    학습용 배치 데이터 생성
    
    Parameters:
    images: 비디오 프레임들의 리스트 (N, height, width, channels)
    keypoints: 키포인트 좌표들의 리스트 (N, n_keypoints * 2)
    seq_length: 입력 시퀀스 길이
    
    Returns:
    X_img: 이미지 시퀀스 배치
    X_kp: 키포인트 시퀀스 배치
    y: 다음 프레임의 키포인트 좌표
    """
    N = len(images) - seq_length  # 가능한 시퀀스 수
    
    X_img = []
    X_kp = []
    y = []
    
    for i in range(N):
        # 입력 시퀀스
        img_seq = images[i:i+seq_length]
        kp_seq = keypoints[i:i+seq_length]
        
        # 타겟 (다음 프레임의 키포인트)
        target = keypoints[i+seq_length]
        
        X_img.append(img_seq)
        X_kp.append(kp_seq)
        y.append(target)
    
    return np.array(X_img), np.array(X_kp), np.array(y)

def load_and_preprocess_data(image_folder_path, keypoints_json_path):
    """
    이미지와 키포인트 데이터를 로드하고 전처리
    
    Parameters:
    image_folder_path: 이미지가 저장된 폴더 경로 (예: 'image_data/image_data_1/')
    keypoints_json_path: 키포인트 JSON 파일 경로 (예: 'skel_data/skel_data_1.json')
    
    Returns:
    images: 전처리된 이미지 배열 (N, height, width, channels)
    keypoints: 키포인트 배열 (N, n_keypoints * 2)
    """
    # 이미지 파일 목록 가져오기 (숫자 순으로 정렬)
    image_files = sorted(
        [f for f in os.listdir(image_folder_path) if f.endswith('.jpg')],
        # key=lambda x: int(x.split('.')[0])  # 파일명의 숫자 부분으로 정렬
        key=lambda x: int(x.split('_')[1].split('.')[0])
    )
    
    # JSON 파일에서 키포인트 데이터 로드
    with open(keypoints_json_path, 'r') as f:
        keypoints_data = json.load(f)
    
    images = []
    keypoints = keypoints_data['keypoints']
    target_size = (128, 128)  # 모델 입력 크기에 맞춤

    # 이미지 로드 및 전처리
    for img_file in image_files:
        # 이미지 로드
        img_path = os.path.join(image_folder_path, img_file)
        img = Image.open(img_path)
        
        # 이미지 크기 조정
        img = img.resize(target_size)
        
        # 이미지를 numpy 배열로 변환 및 정규화 (0-1 범위로)
        img_array = np.array(img) / 255.0
        images.append(img_array)
    
    # # 키포인트 데이터 처리
    # for frame_data in keypoints_data:
    #     # keypoints_data의 구조에 따라 적절히 수정 필요
    #     frame_keypoints = []
    #     print(frame_datadata['keypoints']))
    #     for point in frame_data['keypoints']:  # 실제 JSON 구조에 맞게 수정 필요
    #         frame_keypoints.extend([point['x'], point['y']])

    return np.array(images), np.array(keypoints)

def prepare_training_data(image_folder_path, keypoints_json_path, seq_length=5):
    """
    학습용 데이터 준비
    
    Parameters:
    image_folder_path: 이미지 폴더 경로
    keypoints_json_path: 키포인트 JSON 파일 경로
    seq_length: 시퀀스 길이
    
    Returns:
    X_img: 이미지 시퀀스 배치
    X_kp: 키포인트 시퀀스 배치
    y: 다음 프레임의 키포인트 좌표
    """
    # 데이터 로드
    images, keypoints = load_and_preprocess_data(image_folder_path, keypoints_json_path)
    
    # 학습 배치 생성
    X_img, X_kp, y = create_training_batch(images, keypoints, seq_length)
    
    return X_img, X_kp, y

# 모델 사용 예시
def train_model(image_folder_path, keypoints_json_path):

    # 데이터 준비
    X_img, X_kp, y = prepare_training_data(
        image_folder_path,
        keypoints_json_path,
        seq_length=5
    )
    # 모델 생성
    model = create_hybrid_model()
    
    # 컴파일
    model.compile(
        optimizer='adam',
        loss='mse',  # 키포인트 좌표 예측이므로 MSE 사용
        metrics=['mae','accuracy']
    )

    # 학습
    history = model.fit(
        [X_img, X_kp],
        y,
        batch_size=32,
        epochs=10,
        validation_split=0.2
    )
    
    return model, history

import matplotlib.pyplot as plt

def plot_history(history):
    # 학습 손실 그래프
    plt.figure(figsize=(12, 6))
    
    # 손실 값(loss) 그래프
    plt.subplot(1, 2, 1)
    plt.plot(history.history['loss'], label='Training Loss')
    plt.plot(history.history['val_loss'], label='Validation Loss')
    plt.title('Loss over Epochs')
    plt.xlabel('Epochs')
    plt.ylabel('Loss')
    plt.legend()

    # 정확도(accuracy) 그래프
    plt.subplot(1, 2, 2)
    if 'accuracy' in history.history:
        plt.plot(history.history['accuracy'], label='Training Accuracy')
        plt.plot(history.history['val_accuracy'], label='Validation Accuracy')
        plt.title('Accuracy over Epochs')
        plt.xlabel('Epochs')
        plt.ylabel('Accuracy')
        plt.legend()
    else:
        plt.plot(history.history['accuracy'], label='Training Accuracy')
        plt.title('Accuracy over Epochs')
        plt.xlabel('Epochs')
        plt.ylabel('Accuracy')
        plt.legend()

    plt.tight_layout()
    plt.show()


# 사용 예시
if __name__ == "__main__":
    image_folder = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure\\image_data\\image_data_1"
    keypoints_json = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure\\skel_data\\skel_data_1\\skel_data_001.json"
    
    model, history = train_model(image_folder, keypoints_json)


    # 학습 후 모델 저장
    model.save('C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure\\test_my_model.h5')

    # 학습 그래프 출력
    plot_history(history)
