import tensorflow as tf
from tensorflow.keras.layers import Input, Conv2D, MaxPooling2D, Flatten
from tensorflow.keras.layers import LSTM, Dense, TimeDistributed, Concatenate, BatchNormalization, Dropout, Bidirectional
from tensorflow.keras.models import Model

import numpy as np
import json
import os
from PIL import Image
import cv2

# def create_hybrid_model(
#     seq_length=5,
#     img_height=640,
#     img_width=640,
#     channels=3,
#     n_keypoints=17):
    
#     # 1️⃣ 이미지 입력 스트림 (CNN 적용)
#     img_input = Input(shape=(seq_length, img_height, img_width, channels))
    
#     cnn = TimeDistributed(Conv2D(8, (3, 3), activation='relu', padding='same'))(img_input)
#     cnn = TimeDistributed(BatchNormalization())(cnn)
#     cnn = TimeDistributed(MaxPooling2D((2, 2)))(cnn)

#     cnn = TimeDistributed(Conv2D(16, (3, 3), activation='relu', padding='same'))(cnn)
#     cnn = TimeDistributed(BatchNormalization())(cnn)
#     cnn = TimeDistributed(MaxPooling2D((2, 2)))(cnn)

#     # cnn = TimeDistributed(Conv2D(32, (3, 3), activation='relu', padding='same'))(cnn)
#     # cnn = TimeDistributed(BatchNormalization())(cnn)
#     # cnn = TimeDistributed(MaxPooling2D((2, 2)))(cnn)

#     cnn = TimeDistributed(Flatten())(cnn)
#     cnn = TimeDistributed(Dropout(0.3))(cnn)  # Dropout 추가

#     # 2️⃣ 키포인트 입력 스트림
#     keypoint_input = Input(shape=(seq_length, n_keypoints * 2))

#     # 3️⃣ 두 스트림 결합
#     combined = Concatenate(axis=2)([cnn, keypoint_input])

#     # 4️⃣ LSTM 레이어 (Bidirectional 적용)
#     lstm = LSTM(32)(combined)
    
#     # 5️⃣ 출력 레이어 (다음 프레임 키포인트 예측)
#     output = Dense(n_keypoints * 2)(lstm)

#     # 모델 생성
#     model = Model(inputs=[img_input, keypoint_input], outputs=output)
#     model.summary()

#     return model


# 데이터 배치 생성 함수
def create_training_batch(keypoints, seq_length):
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
    # print(f"image shape : {images.shape}")
    print(f"keypoints shape : {keypoints.shape}")

    videos_cnt = 0
    for i, keypoint_elem in enumerate(keypoints):
        print(f"Element {i} keypoints shape: {np.shape(keypoint_elem)}")
        videos_cnt+=1
    
    X_kp = []
    y = []

    for i, keypoint_elem in enumerate(keypoints):
        
        N = len(keypoint_elem) - seq_length  # 가능한 시퀀스 수
        for i in range(N):
            kp_seq = keypoint_elem[i:i+seq_length]
            
            # 타겟 (다음 프레임의 키포인트)
            target = keypoint_elem[i+seq_length]
            
            X_kp.append(kp_seq)
            y.append(target)
    
    return np.array(X_kp), np.array(y)

def load_and_preprocess_data(kp_file_path):
    """
    이미지와 키포인트 데이터를 로드하고 전처리
    
    Parameters:
    image_folder_path: 'image_data'
    keypoints_json_path:  'skel_data'
    
    이미지가 저장된 폴더 경로 (예: 'image_data/image_data_1/')
    image_data/image_data_1/image_data_001.jpg
    image_data/image_data_1/image_data_002.jpg
    영상하나에서 추출하는 프레임들이 저장되어 있음.

    keypoints_json_path: 키포인트 JSON 파일 경로 (예: 'skel_data/skel_data_1.json')
    json 파일안에는 한 동영상에서 추출한 프레임들의 keypoint 좌표들이 들어있음.
    
    Returns:
    images: 전처리된 이미지 배열 (N, height, width, channels)
    keypoints: 키포인트 배열 (N, n_keypoints * 2)
    """
    
    # # 이미지 파일 목록 가져오기 (숫자 순으로 정렬)
    # image_files = sorted(
    #     [f for f in os.listdir(image_folder_path) if f.endswith('.jpg')],
    #     # key=lambda x: int(x.split('.')[0])  # 파일명의 숫자 부분으로 정렬
    #     key=lambda x: int(x.split('_')[1].split('.')[0])
    # )
    


    # # JSON 파일에서 키포인트 데이터 로드
    # with open(keypoints_json_path, 'r') as f:
    #     keypoints_data = json.load(f)
    
    # images = []
    # keypoints = keypoints_data['keypoints']
    # target_size = (128, 128)  # 모델 입력 크기에 맞춤

    # # 이미지 로드 및 전처리
    # for img_file in image_files:
    #     # 이미지 로드
    #     img_path = os.path.join(image_folder_path, img_file)
    #     img = Image.open(img_path)
        
    #     # 이미지 크기 조정
    #     img = img.resize(target_size)
        
    #     # 이미지를 numpy 배열로 변환 및 정규화 (0-1 범위로)
    #     img_array = np.array(img) / 255.0
    #     images.append(img_array)
    
    # # # 키포인트 데이터 처리
    # # for frame_data in keypoints_data:
    # #     # keypoints_data의 구조에 따라 적절히 수정 필요
    # #     frame_keypoints = []
    # #     print(frame_datadata['keypoints']))
    # #     for point in frame_data['keypoints']:  # 실제 JSON 구조에 맞게 수정 필요
    # #         frame_keypoints.extend([point['x'], point['y']])

    # return np.array(images), np.array(keypoints)
    # image_folders = sorted(
    #     [f for f in os.listdir(image_root) if os.path.isdir(os.path.join(image_root, f))],
    #     key=lambda x: int(x.split('_')[-1])
    # )
    # keypoints_files = sorted(
    #     [f for f in os.listdir(keypoints_root) if f.endswith('.json')],
    #     key=lambda x: int(x.split('_')[-1].split('.')[0])
    # )
    
    all_keypoints = []
    # target_size = (128, 128)
    
    # for kp_file in keypoints_files:
        # kp_file_path = os.path.join(keypoints_root, kp_file)
        
    with open(kp_file_path, 'r') as f:
        keypoints_data = json.load(f)['keypoints']
    
    all_keypoints.append(keypoints_data)
    print(f"all_keypooooint : {all_keypoints}")
    # return np.array(all_images,dtype= object), np.array(all_keypoints,dtype= object)
    return np.array(all_keypoints)

def prepare_training_data(keypoints_json_path, seq_length=5):
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
    keypoints = load_and_preprocess_data(keypoints_json_path)
    print(f"all_keypooooint.shape : {keypoints.shape}")
    
    # 학습 배치 생성
    X_kp, y = create_training_batch(keypoints, seq_length)
    
    return X_kp, y

# 모델 사용 예시
def train_model(X_img, X_kp, y):

    print(f"x_kp shape : {X_kp.shape}")
    print(f"y shape : {y.shape}")
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
        batch_size=2,
        epochs=100,
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

import numpy as np
from tensorflow.keras.models import load_model

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


# 사용 예시
if __name__ == "__main__":
# def train(test_image_folder,test_keypoints_json):
    with tf.device("/gpu:0"):
        # image_folder = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure\\image_data"
        # keypoints_json = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure\\skel_data"
        
        # # 데이터 준비
        # X_img, X_kp, y = prepare_training_data(
        #     image_folder,
        #     keypoints_json,
        #     seq_length=5
        # )


        # model, history = train_model(X_img, X_kp, y)


        # # 학습 후 모델 저장
        # model.save('C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure\\test_my_model.h5')

        # 학습된 모델 로드
        # model = load_model('C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure\\test_my_model.h5')

        model = tf.keras.models.load_model('C:\\Users\\SSAFY\\Desktop\\hangi\\Bowling_Coach_Web\\model\\h5\\CNN_LSTM_V1.h5')
        
        model.summary()

        test_image_folder = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure\\image_data\\image_data_64"
        test_keypoints_json = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure\\skel_data\\skel_data_064.json"
        
        # 데이터 준비
        test_X_kp, test_y = prepare_training_data(
            # test_image_folder,
            test_keypoints_json,
            seq_length=5
        )

        # print("test_images type:", type(test_X_img))
        # print("test_images dtype:", test_X_img.dtype if isinstance(test_X_img, np.ndarray) else "Not a NumPy array")
        # print("test_images shape:", test_X_img.shape if isinstance(test_X_img, np.ndarray) else "Not a NumPy array")

        print("test_keypoints type:", type(test_X_kp))
        print("test_keypoints dtype:", test_X_kp.dtype if isinstance(test_X_kp, np.ndarray) else "Not a NumPy array")
        print("test_keypoints shape:", test_X_kp.shape if isinstance(test_X_kp, np.ndarray) else "Not a NumPy array")

        print("test_keypoints type:", type(test_y))
        print("test_keypoints dtype:", test_y.dtype if isinstance(test_y, np.ndarray) else "Not a NumPy array")
        print("test_keypoints shape:", test_y.shape if isinstance(test_y, np.ndarray) else "Not a NumPy array")

        windowsize = 5
        test_X_kp = test_X_kp.reshape((test_X_kp.shape[0], windowsize, 51, 1))
        test_y = test_y.reshape((test_y.shape[0], 51))

        # 모델 테스트 및 유사도 측정
        similarity_scores,showing_scores = test_model_on_video(model, test_X_kp, test_y)
        
        # 평균 유사도 계산
        avg_similarity = np.mean(similarity_scores)
        
        # 전체 점수 계산
        avg_scores = np.mean(showing_scores)
        print(f"showing_scores shape : {showing_scores.shape}")
        print(showing_scores)

        print(f"Similarity Scores: {similarity_scores}")
        print(f"Average Similarity Score: {avg_similarity:.1f}")
    

        print(avg_scores)
        print(avg_similarity)

            # 이미지 파일 목록 가져오기 (숫자 순으로 정렬)
        image_files = sorted(
            [f for f in os.listdir(test_image_folder) if f.endswith('.jpg')],
            # key=lambda x: int(x.split('.')[0])  # 파일명의 숫자 부분으로 정렬
            key=lambda x: int(x.split('_')[1].split('.')[0])
        )
        
        images = []

        frames_with_keypoints = []
        image_files = image_files[5:len(image_files)]
        target_size = (640, 640)  # 모델 입력 크기에 맞춤

        # 저장 경로 설정
        base_dir = 'C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure'
        opt_path = os.path.join(base_dir, "joint")
        os.makedirs(opt_path, exist_ok=True)  # 폴더가 없으면 생성
        # 이미지 로드 및 전처리
        for i, img_file in enumerate(image_files):
            # 이미지 로드
            img_path = os.path.join(test_image_folder, img_file)
            img = cv2.imread(img_path)

            if img is None:
                print(f"이미지를 불러올 수 없습니다: {img_path}")
                continue  # 이미지 로드 실패 시 건너뛰기

            # 이미지 크기 조정
            img = cv2.resize(img, target_size)  # OpenCV resize 사용

            # 유사도 점수 기반 텍스트 추가
            accuracy_text = f"Accuracy: {showing_scores[i]:.4f}"
    
            if similarity_scores[i] > 50:
                text_color = (0, 255, 0)  # 초록색
            else:
                text_color = (0, 0, 255)  # 빨간색

            # 이미지 위에 텍스트 추가
            cv2.putText(
                img,  # ✅ 올바른 입력 (numpy 배열)
                accuracy_text,  # 표시할 텍스트
                (10, 50),  # 텍스트 위치
                cv2.FONT_HERSHEY_COMPLEX, 1.5,  # 폰트와 크기
                text_color, 2  # 색상과 두께
            )
            frames_with_keypoints.append(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))

            
            cv2.imwrite(f"{opt_path}/frame_{i:03d}.jpg", img)


        print("IN to_mp4 --- ")
        print(images[0].shape)
        height, width, _ = frames_with_keypoints[0].shape
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')  # mp4v 코덱 사용
        input_file_name = os.path.basename("similarity_video_output")  # 입력 파일 이름 (경로 제외)
        output_file = os.path.join(base_dir, input_file_name)  # 출력 폴더에 동일한 파일 이름 사용
        video_writer = cv2.VideoWriter(output_file, fourcc, 30, (width, height))

        for image in images:
            # 이미지 데이터를 BGR로 변환 (OpenCV는 BGR 포맷을 사용)
            bgr_image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
            video_writer.write(bgr_image)

        video_writer.release()
        print(f"Video saved as {output_file}")
        # 학습 그래프 출력
        # plot_history(history)
