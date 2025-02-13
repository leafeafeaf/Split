from tqdm import tqdm
import torch 
import cv2
from preprocessing.video_preprocess import *
from training.input import InputData
from torch.utils.data import DataLoader
from config import device 

# similarity_measure 폴더 경로를 추가
classfication_bowling_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "posture_correct"))
sys.path.append(classfication_bowling_path)

from infer import infer_correction

# 데이터 배치 생성 함수
def create_training_batch(keypoints, seq_length):
    """
    학습용 배치 데이터 생성
    
    Parameters:
    keypoints: 키포인트 좌표들의 리스트 (N, n_keypoints * 2)
    seq_length: 입력 시퀀스 길이
    
    Returns:
    X_img: 이미지 시퀀스 배치
    X_kp: 키포인트 시퀀스 배치
    y: 다음 프레임의 키포인트 좌표
    """
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

def prepare_training_data(bowling_keypoints_list, seq_length=5):
    """
    학습용 데이터 준비
    
    Parameters:
    keypoints_json_path: 키포인트 JSON 파일 경로
    seq_length: 시퀀스 길이
    
    Returns:
    X_kp: 키포인트 시퀀스 배치
    y: 다음 프레임의 키포인트 좌표
    """
    # 데이터 로드
    keypoints = np.array(bowling_keypoints_list)
    print(keypoints.shape)
    keypoints = keypoints.reshape((1,keypoints.shape[0], keypoints.shape[1]))
    # 학습 배치 생성
    X_kp, y = create_training_batch(keypoints, seq_length)
    
    return X_kp, y

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


def infer():
    from training.visualization import net
    net.load_state_dict(torch.load('best_1330.pth'))
    net.eval()
    out_img_list = []
    dataset = []
    status = 'None'

    print('시퀀스 데이터 분석 중...')
    test_video_path = 'C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\oCam\\bowlingwallking\\seungmin.mp4'  # 테스트용 영상
    output_folder = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\output"
    # bowling_folder = 'C:\\Users\\SSAFY\\Desktop\\infer_out\\LSTM_Modified\\bowling'

    # walking / bowling 판별 , 이미지 저장용 keypoint 추출
    (skel_dataset,skel_datasetof2, img_list, bowling_start) = process_video_infer(test_video_path, output_folder)

    print(f"len(skel_dataset) : {len(skel_dataset)}")

    # 영상 이미지별 skeleton 리스트
    # 영상 이미지별 skeleton까지 입힌 이미지 리스트

    test_list = []
    bowling_end = 0
    walking_img_list = []
    bowling_img_list = []
    bowling_skel_list = []
    for idx in tqdm(range(len(skel_datasetof2))):
        print(f"현재 idx: {idx}")    
        # length = 10으로 해보고 안되면 20
        length = 7
        
        test_list.append(skel_datasetof2[idx])
        # print(f"스켈레톤 배열 크기 : {len(skel_dataset[idx])}") # expected : (17,2)
        # print(f"이미지 배열 크기 : {len(img_list[idx])}") # expected : (17,2)
        # print(f"현재 리스트 길이: {len(test_list)}")
        if len(test_list) == length:
            dataset = []
            dataset.append({'key': 0, 'value': test_list})
            dataset = InputData(dataset)
            dataset = DataLoader(dataset)
            test_list = []
            # 각 이미지마다 skeleton 입히는 작업 추가하기 !!
            
            for data, label in dataset:
                data = data.to(device)
                with torch.no_grad():
                    result = net(data)
                    print(f"현재 : {result}")
                    _, out = torch.max(result, 1)
                    if out.item() == 1: status = 'Walking'
                    else: 
                        status = 'Bowling'
                        bowling_start = min(idx, bowling_start)
                        bowling_end = max(idx, bowling_end)
                    print(f"{idx}에서 학습된 결과: {status}")
            print(f"볼링 시작 idx: {bowling_start}, 끝 idx: {bowling_end}")
        cv2.putText(img_list[idx], status, (0, 30), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (0, 0, 255), 2)
        # cv2.imshow(img_list[idx])
        out_img_list.append(img_list[idx])

    # walking img list에 저장
    for i in range(0, bowling_start):
        target_size = (640, 640)
        img = cv2.resize(img_list[i], target_size)  # OpenCV resize 사용

        walking_img_list.append(img)
    print(f"len(walking_img_list) : {len(walking_img_list)}")

    
    # bowling img list에 저장
    for i in range(bowling_start, bowling_end + 1):
        bowling_img_list.append(img_list[i])       # bowling_img_data
        bowling_skel_list.append(skel_dataset[i])  # bowling_skel_data 
        print(i)
    print(f"len(walking_img_list) : {len(bowling_img_list)}")
    # 이쪽에서 유사도 뽑아내서 -> 유사도를 기반으로 bowling_img_list에 있는 이미지에다가 유사도 붙여서 반환
    # ---------------- 회귀 기반 자세 교정 ---------------------
    
    infer_correction(skel_datasetof2[bowling_end])

    #  ---------------- 유사도 --------------------- 

    # 모델 로드
    model = tf.keras.models.load_model('C:\\Users\\SSAFY\\Desktop\\hangi\\Bowling_Coach_Web\\model\\h5\\CNN_LSTM_V1.h5')
    model.summary()

    # 데이터 준비
    test_X_kp, test_y = prepare_training_data(
        bowling_skel_list, # [[51],[51], ... 볼링중인 IDX 범위까지 ]
        seq_length=5
    )
    
    windowsize = 5
    print(f"test_X의 키포인트 배열 크기: {test_X_kp.shape}")
    print(f"test_X의 키포인트  0 배열 크기: {test_X_kp.shape[0]}")
    test_X_kp = test_X_kp.reshape((test_X_kp.shape[0], windowsize, 51, 1))
    test_y = test_y.reshape((test_y.shape[0], 51))

    # 모델 테스트 및 유사도 측정
    similarity_scores,showing_scores = test_model_on_video(model, test_X_kp, test_y)
    
    
    # 평균 유사도 계산
    avg_similarity = np.mean(similarity_scores)
    
    # 전체 점수 계산
    avg_scores = np.mean(showing_scores)

    # 0보다 작으면 0, 100보다 크면 100으로 저장
    showing_scores = np.clip(showing_scores, 0, 100)

    print(f"showing_scores shape : {showing_scores.shape}")
    print(showing_scores)
    print(f"Similarity Scores: {similarity_scores}")
    print(f"Average Similarity Score: {avg_similarity:.1f}")

    print(avg_scores) ##### json 저장 필요요
    print(avg_similarity)

    # --------------------------------------------

    # ----------------이미지에 유사도 표시하기--------------------

    target_size = (640, 640)  # 모델 입력 크기에 맞춤
    bowling_img_list = bowling_img_list[5:len(bowling_img_list)]

    frames_with_keypoints = []

    # 저장 경로 설정
    # base_dir = 'C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\similarity_measure'
    # opt_path = os.path.join(base_dir, "joint")
    # os.makedirs(opt_path, exist_ok=True)  # 폴더가 없으면 생성
    # 이미지 로드 및 전처리
    for i, img in enumerate(bowling_img_list):
        # 이미지 로드
        # img_path = os.path.join(test_image_folder, img_file)
        # img = cv2.imread(img_path)
        if img is None:
            print(f"이미지를 불러올 수 없습니다 ")
            continue  # 이미지 로드 실패 시 건너뛰기
        # 이미지 크기 조정
        img = cv2.resize(img, target_size)  # OpenCV resize 사용
        # 유사도 점수 기반 텍스트 추가
        accuracy_text = f"Accuracy: {showing_scores[i]:.1f}"

        if showing_scores[i] > 50:
            text_color = (0, 255, 0)  # 초록색
        else:
            text_color = (0, 0, 255)  # 빨간색
        # 이미지 위에 텍스트 추가
        cv2.putText(
            img,  # ✅ 올바른 입력 (numpy 배열)
            accuracy_text,  # 표시할 텍스트
            (0, 100),  # 텍스트 위치
            cv2.FONT_HERSHEY_SIMPLEX, 1.5,  # 폰트와 크기
            text_color, 2  # 색상과 두께
        )

        # frames_with_keypoints.append(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
        frames_with_keypoints.append(img)
    # -----------------------------------------------------------
    # -------------------walking + bowling(keypoint) 합치기 -------------

    for bowling_with_similarity in frames_with_keypoints:
        walking_img_list.append(bowling_with_similarity)

    walking_img_list = np.array(walking_img_list)
    # ---------------------------------------------------

    # 저장할 파일 경로
    file_path = "C:\\Users\\SSAFY\\Desktop\\S12P11B202\\AI\\output\\similarity_Score.json"
    # 점수를 딕셔너리 형태로 준비
    data = {"score": float(avg_scores)}

    # JSON 파일로 저장
    with open(file_path, 'w') as f:
        json.dump(data, f, indent=4)

    # to_mp4(out_img_list, fps=30, input_file_path=test_video_path, output_folder=output_folder)
    # to_mp4(bowling_img_list, fps=30, input_file_path=test_video_path, output_folder=bowling_folder)
    
    to_mp4(walking_img_list, fps=30, input_file_path=test_video_path, output_folder=output_folder)

    return bowling_skel_list
