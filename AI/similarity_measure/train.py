import tensorflow as tf
from tensorflow.keras.layers import Input, Flatten
from tensorflow.keras.layers import LSTM, Dense, TimeDistributed

import numpy as np
import json
import os

import tensorflow as tf
from tensorflow.keras.models import Model
from tensorflow.keras.layers import (
    Input, TimeDistributed, Conv1D, Flatten, LSTM, Dense
)
import matplotlib as plt
import tensorflow.keras.backend as K

def create_hybrid_model(n_sequence, seq_length=5, n_keypoints=51, channels=1):
    """
    Hybrid CNN-LSTM 모델 생성

    Parameters:
    - n_sequence: 총 시퀀스 개수 (사용되지 않음)
    - seq_length: 입력 시퀀스 길이 (default=5)
    - n_keypoints: 각 프레임의 키포인트 수 (default=51)
    - channels: 입력 채널 수 (default=1)

    Returns:
    - Keras 모델 객체
    """

    # 입력 (None, 5, 51, 1) 형태
    input_layer = Input(shape=(seq_length, n_keypoints, channels))

    # CNN 적용 (TimeDistributed Conv1D)
    x = TimeDistributed(Conv1D(filters=64, kernel_size=3, activation='relu', padding="same"))(input_layer)
    x = TimeDistributed(Flatten())(x)  # (None, 5, 3264)

    # LSTM 레이어 적용 (입력 차원 수정)
    x = LSTM(50, return_sequences=True)(x)  # (None, 5, 50)
    x = LSTM(50, return_sequences=False)(x)  # (None, 50)

    # 출력 레이어 (다음 프레임의 51개 키포인트 예측)
    output_layer = Dense(n_keypoints, activation='linear')(x)  # (None, 51)

    # 모델 생성
    model = Model(inputs=input_layer, outputs=output_layer)
    model.compile(optimizer='adam', loss='mse')

    return model

# 모델 생성 및 요약 출력
model = create_hybrid_model(n_sequence=5000)
model.summary()

def create_training_batch(keypoints, seq_length):
    """
    학습용 배치 데이터 생성
    """
    N = len(keypoints) - seq_length  # 가능한 시퀀스 수

    X_kp = []
    y = []

    for i in range(N):
        kp_seq = keypoints[i:i+seq_length]  # (seq_length, 51) 형태
        target = keypoints[i+seq_length]  # (51,) 형태

        X_kp.append(kp_seq)
        y.append(target)

    return X_kp, y

def load_and_preprocess_data(keypoints_root, seq_length):
    """
    키포인트 데이터를 로드하고 학습용 배치 생성
    """
    # JSON 파일 목록 정렬
    keypoints_files = sorted(
        [f for f in os.listdir(keypoints_root) if f.endswith('.json')],
        key=lambda x: int(x.split('_')[-1].split('.')[0])
    )

    X_kp = []
    y = []

    # 모든 키포인트 데이터 로드 및 학습 데이터 생성
    for kp_file in keypoints_files:
        kp_file_path = os.path.join(keypoints_root, kp_file)

        with open(kp_file_path, 'r') as f:
            keypoints_data = np.array(json.load(f)['keypoints'])

        # 학습 배치 생성
        X_batch, y_batch = create_training_batch(keypoints_data, seq_length)
        X_kp.extend(X_batch)
        y.extend(y_batch)

    return np.array(X_kp), np.array(y)

def prepare_training_data(keypoints_json_folder, seq_length=5):
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
    X_kp, y = load_and_preprocess_data(keypoints_json_folder, seq_length)
    # print(f"all_keypooooint.shape : {keypoints.shape}")
    
    print(f" X_kp.shape : {np.shape(X_kp)}")
    print(f" y.shape : {np.shape(y)}")
    
    
    return X_kp, y

# R² (결정계수) 정의
def r2_score(y_true, y_pred):
    SS_res = K.sum(K.square(y_true - y_pred))
    SS_tot = K.sum(K.square(y_true - K.mean(y_true)))
    return 1 - SS_res / (SS_tot + K.epsilon())

# RMSE 정의
def rmse(y_true, y_pred):
    return K.sqrt(K.mean(K.square(y_true - y_pred)))

# 모델 학습 함수
def train_model(X_kp, y):
    print(f"x_kp shape : {X_kp.shape}")  # (1260, 5, 51)
    print(f"y shape : {y.shape}")  # (1260, 51)

    # 모델 생성 (입력 shape 지정)
    model = create_hybrid_model(n_sequence=X_kp.shape[0], seq_length=5, n_keypoints=51)

    # 컴파일
    model.compile(
        optimizer='adam',
        loss='mse',  # 키포인트 좌표 예측이므로 MSE 사용
        metrics=['mae', r2_score, rmse]  # accuracy 제거
    )

    # 학습
    history = model.fit(
        X_kp, y,
        batch_size=16,  # batch_size=2 → 16으로 변경
        epochs=50,
        validation_split=0.2,  # 20% 데이터를 검증용으로 사용
        verbose=1  # 학습 과정 출력
    )

    return model, history

def plot_history(history):
    """ 학습 과정에서 손실(loss), MAE, RMSE, R² 그래프 출력 """
    metrics = ['loss', 'mae', 'rmse', 'r2_score']
    titles = ['MSE Loss', 'Mean Absolute Error (MAE)', 'Root Mean Squared Error (RMSE)', 'R² Score']
    
    plt.figure(figsize=(12, 8))

    for i, metric in enumerate(metrics):
        plt.subplot(2, 2, i + 1)
        plt.plot(history.history[metric], label=f'Train {metric}')
        plt.plot(history.history[f'val_{metric}'], label=f'Val {metric}')
        plt.title(titles[i])
        plt.xlabel('Epochs')
        plt.ylabel(metric)
        plt.legend()
        plt.grid()

    plt.tight_layout()
    plt.show()

def calculate_similarity(pred_kp, actual_kp):
    """
    예측된 키포인트와 실제 키포인트 간의 유사도 계산 (MSE 기반)
    """
    mse = np.mean(np.square(pred_kp - actual_kp), axis=1)  # MSE 계산
    print(f"mse: {mse}")
    similarity_scores = 1 / (1 + mse)  # 값이 작을수록 유사도 높음

    showing_scores = 1000 * (1 - np.log1p(mse) / np.log1p(5000))

    return similarity_scores,showing_scores  # shape: (batch_size,)

def test_model_on_video(model,test_keypoints, actual_next_keypoints):
    """
    모델을 사용하여 동영상의 모든 5프레임 시퀀스로 다음 프레임 예측 후, 실제 키포인트와 비교하여 유사도 계산
    
    Parameters:
    - model: 학습된 모델
    - test_images: 일반인의 5개 프레임 이미지 시퀀스들 (batch_size, 5, height, width, channels)
    - test_keypoints: 일반인의 5개 프레임 키포인트 시퀀스들 (batch_size, 5, n_keypoints * 2)
    - actual_next_keypoints: 일반인의 실제 6번째 프레임 키포인트들 (batch_size, n_keypoints * 2)
    
    Returns:
    - 평균 유사도 점수 (0~1)
    """

    # 모델을 사용하여 6번째 프레임의 키포인트 예측
    # predicted_keypoints = model.predict([test_images, test_keypoints])  # shape: (batch_size, n_keypoints * 2)
    predicted_keypoints = model.predict(test_keypoints)  # shape: (batch_size, n_keypoints * 2)

    # MSE 기반 유사도 계산
    similarity_scores, showing_scores = calculate_similarity(predicted_keypoints, actual_next_keypoints)

    
    return similarity_scores, showing_scores

# 키포인트 데이터를 정규화 (예: 0~1 범위로 변환)
def normalize_keypoints(keypoints):
    keypoints = np.array(keypoints)
    max_value = np.max(keypoints)  # 최댓값 찾기
    min_value = np.min(keypoints)  # 최솟값 찾기
    return (keypoints - min_value) / (max_value - min_value + 1e-8)  # 작은 값 추가해 0 나눗셈 방지

# 사용 예시
if __name__ == "__main__":
    with tf.device("/gpu:0"):
        # image_folder = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure\\image_data"
        keypoints_json = "skel_folder"
        
        # 데이터 준비
        X_kp, y = prepare_training_data(
            # image_folder,
            keypoints_json,
            seq_length=5
        )

        X_kp = normalize_keypoints(X_kp)
        y = normalize_keypoints(y)

        model, history = train_model(X_kp, y)


        # 학습 후 모델 저장
        model.save('test_my_model.h5')

        # 학습된 모델 결과 그래프
        plot_history(history)
        
        model.summary()

        windowsize = 5
