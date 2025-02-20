from preprocessing.skeleton import *
import cv2
import tensorflow as tf
import numpy as np
import os
from preprocessing.labeling import labeling
import json
from preprocessing.save_to_Json import save_skel_to_json
import sys
from preprocessing.save_to_Json import save_to_json

# similarity_measure 폴더 경로를 추가
similarity_measure_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "..","..", "similarity_measure"))
sys.path.append(similarity_measure_path)

# 이제 train.py를 import 가능
# from train import train

def process_videos(input_folder, output_folder, fps=30):
    cnt = 1
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    video_files = [f for f in os.listdir(input_folder) if f.endswith('.mp4')]
    video_files.sort(key=lambda f: int(''.join(filter(str.isdigit, f))))
    video_idx = 119

    for video_file in video_files:
        print(video_file)
        # video_path = video_file
        video_path = os.path.join(input_folder, video_file)
        output_path = os.path.join(output_folder, video_file)
        skel_per_video = []
        # 비디오 캡처 객체 생성 
        cap = cv2.VideoCapture(video_path)
        fps = cap.get(cv2.CAP_PROP_FPS)  # 원본 비디오 FPS 가져오기
        print(f"Original FPS: {fps}")
        # 프레임을 저장할 리스트 초기화
        frames = []


        #---------------유사도 판단 데이터 저장-------------
        # 'similar' 폴더 안에 'image_data' 폴더 경로 설정
        output_folder = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', 'similarity_measure','image_data',f"image_data_{video_idx}")
        output_folder = os.path.abspath(output_folder)
        skel_output_folder = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', 'similarity_measure','skel_data')
        skel_output_folder = os.path.abspath(skel_output_folder)
        

        # 폴더가 없으면 생성
        if not os.path.exists(output_folder):
            os.makedirs(output_folder)
        
        #---------------------------------------------------

        # 비디오의 모든 프레임을 읽기
        frame_idx = 0
        while cap.isOpened():
            ret, frame = cap.read()
            if not ret:
                break
            
            # BGR -> RGB 변환 (OpenCV는 기본적으로 BGR로 이미지를 읽음)
            frame = cv2.resize(frame, (128, 128))  # 프레임 크기 조정
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            
            
            # 프레임을 리스트에 저장
            frames.append(frame)
            
            frame_idx += 1

        # 비디오 캡처 객체 해제
        cap.release()

        # 프레임 리스트가 비어 있는지 확인
        if not frames:
            print(f"No frames collected for {video_file}. Skipping...")
            continue

        # NumPy 배열로 변환
        frames_np = np.array(frames)

        # 4차원 배열이 아니면 에러 발생
        if len(frames_np.shape) != 4:
            print(f"Frames array is not 4-dimensional for {video_file}. Skipping...")
            continue

        # TensorFlow 텐서로 변환
        image = tf.convert_to_tensor(frames_np, dtype=tf.uint8)
        # 텐서 모양 확인
        num_frames, image_height, image_width, channels = image.shape
        # print(f"Processing video: {video_file}")
        # print(f"Number of frames: {num_frames}, Image height: {image_height}, Image width: {image_width}, Channels: {channels}")

        # Crop 영역 설정 (여기서 사용하는 함수들을 정의해두셔야 합니다)
        crop_region = init_crop_region(image_height, image_width)

        output_images = []

        # 한 동영상의 frame별 keypoint좌표를 저장할 리스트
        
        # bar = display(progress(0, num_frames-1), display_id=True)
        idx = 0
        for frame_idx in range(num_frames):
            keypoint_dataset = [] # keypoint 좌표가 들어가는 배열
            # print(frame_idx)
            keypoints_with_scores = run_inference(
            movenet, image[frame_idx, :, :, :], crop_region,
            crop_size=[input_size, input_size])

            (keypoint_xy,output_images_input,keypoint_scores) =draw_prediction_on_image(
            image[frame_idx, :, :, :].numpy().astype(np.int32),
            keypoints_with_scores, crop_region=None,
            close_figure=True, output_image_height=300)
        
            print(f"keyopint_input : {output_images_input}")

            # print(f"keypoint_with_scores : {keypoints_with_scores}")
            # print(f"keypoint_with_scores shape : {keypoints_with_scores.shape}")

            # # print(keypoint_xy) # 키 포인트 출력 
            # if keypoint_xy is not None and keypoint_xy.shape[0] == 17:   # 17개 관절이 모두 있어야 함
                
            #     for keypoint_dict_idx in range(len(keypoint_xy)):
            #         for keypoint_xy_idx in range(len(keypoint_xy[0])):
            #             keypoint_dataset.append(keypoint_xy[keypoint_dict_idx][keypoint_xy_idx])
            #             keypoint_dataset.append(keypoints_with_scores)

            # if len(keypoint_dataset) == 34:
            #     # 각 프레임을 이미지 파일로 저장 (프레임 번호를 파일명에 추가) ---- 유사도 판단단
            #     frame_filename = os.path.join(output_folder, f'frame_{frame_idx}.jpg')
            #     cv2.imwrite(frame_filename, cv2.cvtColor(frames[frame_idx], cv2.COLOR_RGB2BGR))  # 다시 BGR로 변환하여 저장
            #     skel_per_video.append(keypoint_dataset)
            # output_images.append(output_images_input)
            # crop_region = determine_crop_region(
            # keypoints_with_scores, image_height, image_width)
            # print(keypoint_xy) # 키 포인트 출력 
            if keypoint_xy is not None and keypoint_xy.shape[0] == 17:   # 17개 관절이 모두 있어야 함
                idx += 1
                for keypoint_dict_idx in range(len(keypoint_xy)):
                    for keypoint_xy_idx in range(len(keypoint_xy[0])):
                        keypoint_dataset.append(keypoint_xy[keypoint_dict_idx][keypoint_xy_idx])
                    keypoint_dataset.append(keypoint_scores[keypoint_dict_idx])

            # if len(keypoint_dataset) == 34:
                # 각 프레임을 이미지 파일로 저장 (프레임 번호를 파일명에 추가) ---- 유사도 판단단
                frame_filename = os.path.join(output_folder, f'frame_{idx}.jpg')
                cv2.imwrite(frame_filename, cv2.cvtColor(output_images_input, cv2.COLOR_RGB2BGR))  # 다시 BGR로 변환하여 저장
                # cv2.imwrite(frame_filename, cv2.cvtColor(frames[frame_idx], cv2.COLOR_RGB2BGR))  # 다시 BGR로 변환하여 저장
                skel_per_video.append(keypoint_dataset)
            output_images.append(output_images_input)
            crop_region = determine_crop_region(
            keypoints_with_scores, image_height, image_width)
            # bar.update(progress(frame_idx, num_frames-1))
        # output_images.append(draw_prediction_on_image(
        #       image[frame_idx, :, :, :].numpy().astype(np.int32),
        #       keypoints_with_scores, crop_region=None,
        #       close_figure=True, output_image_height=300))
        output = np.stack(output_images, axis=0)
        print(f"skel_per_video : {skel_per_video}")
        print(f"skel_per_video_len : {len(skel_per_video)}")

        skel_output = {
            'keypoints': skel_per_video
        }

        # 스켈레톤 데이터 저장 ----- 유사도 판단단
        save_skel_to_json(skel_output,skel_output_folder,video_idx)
        video_idx +=1

    skel_filename = os.path.join(skel_output_folder, f'skel_data_{video_idx:03d}.json')


    # train(output_folder,skel_filename)
        
        # labeling(video_file,skel_per_video)

        # skel_dataset.append(skel_per_video)

    # return skel_dataset



def interpolate_keypoints_between_frames(prev_keypoints, next_keypoints, gap, num_keypoints=17):
    """두 프레임 사이의 키포인트를 비례적으로 보간합니다."""
    keypoints_interpolated = []
    
    for i in range(num_keypoints):
        prev_keypoint = prev_keypoints[i]
        next_keypoint = next_keypoints[i]
        
        # X, Y 좌표와 스코어를 비례적으로 보간
        interpolated_keypoint = prev_keypoint + (next_keypoint - prev_keypoint) * np.linspace(0, 1, gap)
        keypoints_interpolated.append(interpolated_keypoint)
        
    return np.array(keypoints_interpolated)

def process_video_infer(video_path, output_folder, fps=30):
    # ✅ output_folder 매개변수 추가
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    # 비디오 캡처 객체 생성
    cap = cv2.VideoCapture(video_path)
    original_fps = cap.get(cv2.CAP_PROP_FPS)  # 원본 비디오 FPS 가져오기
    print(f"Original FPS: {original_fps}")

    # 프레임을 저장할 리스트 초기화
    frames = []
    frames_128 = []

    # 비디오의 모든 프레임을 읽기
    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break

        frame_128 = cv2.resize(frame, (128, 128))  # 프레임 크기 조정
        frame = cv2.resize(frame, (640, 640))  # 프레임 크기 조정

        # BGR -> RGB 변환 (OpenCV는 기본적으로 BGR로 이미지를 읽음)
        frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        frame_128 = cv2.cvtColor(frame_128, cv2.COLOR_BGR2RGB)

        frames.append(frame)
        frames_128.append(frame_128)

    # 비디오 캡처 객체 해제
    cap.release()

    # NumPy 배열로 변환
    frames_np = np.array(frames)

    # ✅ 프레임이 비어있는 경우 예외 처리 추가
    if frames_np.size == 0:
        print(f"No frames found in {video_path}. Skipping...")
        return None, None

    # TensorFlow 텐서로 변환
    image = tf.convert_to_tensor(frames_np, dtype=tf.uint8)

    # 텐서 모양 확인
    num_frames, image_height, image_width, channels = image.shape
    print(f"Processing video: {video_path}")
    print(f"Number of frames: {num_frames}, Image height: {image_height}, Image width: {image_width}, Channels: {channels}")

    # Crop 영역 설정
    crop_region = init_crop_region(image_height, image_width)

    output_images = []
    skel_datasetof2 = []
    skel_dataset = []

    # 키포인트가 17개인 프레임 찾기
    keypoints_with_scores_per_frame = []

    for frame_idx in range(num_frames):
        keypoint_datasetof2 = []
        keypoint_dataset = []

        # 키포인트 추론 수행
        keypoints_with_scores = run_inference(
            movenet, image[frame_idx, :, :, :], crop_region,
            crop_size=[input_size, input_size]
        )

        # 키포인트 시각화 및 데이터 추출
        keypoint_xy, output_image, keypoint_scores = draw_prediction_on_image(
            image[frame_idx, :, :, :].numpy().astype(np.int32),
            keypoints_with_scores, crop_region=None,
            close_figure=True, output_image_height=300
        )

        # 키포인트 데이터 저장
        if keypoint_xy is not None and keypoint_xy.shape[0] == 17:
            print(f"frame_idx = {frame_idx}")
            for keypoint_dict_idx in range(len(keypoint_xy)):
                if keypoint_xy.ndim > 1:  # keypoint_xy가 2D 배열일 때
                    keypoint_dataset.append(keypoint_xy[keypoint_dict_idx][0])  # X 좌표
                    keypoint_dataset.append(keypoint_xy[keypoint_dict_idx][1])  # Y 좌표
                else:  # keypoint_xy가 1D 배열일 경우
                    keypoint_dataset.append(keypoint_xy[keypoint_dict_idx])  # 단일 좌표

                keypoint_dataset.append(keypoint_scores[keypoint_dict_idx])  # 스코어 추가

                if keypoint_xy.ndim > 1:
                    keypoint_datasetof2.append(keypoint_xy[keypoint_dict_idx][0])
                    keypoint_datasetof2.append(keypoint_xy[keypoint_dict_idx][1])
                else:
                    keypoint_datasetof2.append(keypoint_xy[keypoint_dict_idx])

            skel_dataset.append(keypoint_dataset)  # keypoint_dataset : [51개]
            skel_datasetof2.append(keypoint_datasetof2)
        else:
            print(f"frame_idx = {frame_idx}에서 키포인트 데이터가 유효하지 않습니다.")

        output_images.append(output_image)

        # 새로운 crop_region 업데이트
        crop_region = determine_crop_region(
            keypoints_with_scores, image_height, image_width
        )

    # 보간 처리: 두 17개 키포인트가 있는 프레임 사이의 프레임을 보간
    interpolated_keypoints = []
    for i in range(1, len(keypoints_with_scores_per_frame)):
        prev_keypoints, prev_scores = keypoints_with_scores_per_frame[i - 1]
        next_keypoints, next_scores = keypoints_with_scores_per_frame[i]

        if prev_keypoints is not None and next_keypoints is not None:
            # 두 17개 키포인트가 있는 프레임 사이의 간격 계산
            gap = i - (i - 1)  # 두 프레임 사이의 간격
            interpolated = interpolate_keypoints_between_frames(prev_keypoints, next_keypoints, gap)
            interpolated_keypoints.append(interpolated)

    # 최종 키포인트 데이터 처리
    for keypoint_xy, keypoint_scores in keypoints_with_scores_per_frame:
        if keypoint_xy is None:  # 보간된 키포인트가 있을 경우
            if interpolated_keypoints:  # 보간된 키포인트가 남아 있다면
                interpolated_data = interpolated_keypoints.pop(0)
                keypoint_xy = interpolated_data[0]  # 첫 번째 값은 keypoint_xy
                keypoint_scores = interpolated_data[1]  # 두 번째 값은 keypoint_scores
        else:
            keypoint_xy, keypoint_scores = None, None

        if keypoint_xy is not None:
            for keypoint_dict_idx in range(len(keypoint_xy)):
                keypoint_dataset.append(keypoint_xy[keypoint_dict_idx][0])
                keypoint_dataset.append(keypoint_xy[keypoint_dict_idx][1])
                keypoint_dataset.append(keypoint_scores[keypoint_dict_idx])

                keypoint_datasetof2.append(keypoint_xy[keypoint_dict_idx][0])
                keypoint_datasetof2.append(keypoint_xy[keypoint_dict_idx][1])

            skel_dataset.append(keypoint_dataset)
            skel_datasetof2.append(keypoint_datasetof2)

    return skel_dataset, skel_datasetof2, output_images, num_frames


def release_capture(input_folder, output_folder, fps=30):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    video_files = [f for f in os.listdir(input_folder) if f.endswith('.mp4')]
    video_files.sort(key=lambda f: int(''.join(filter(str.isdigit, f))))  # 숫자로 정렬
    print(f"현재 폴더의 비디오 개수: {len(video_files)}")
    video_idx = 52
    for video_file in video_files:
        print(f"Processing {video_file}...")
        video_path = os.path.join(input_folder, video_file)

        cap = cv2.VideoCapture(video_path)
        fps = cap.get(cv2.CAP_PROP_FPS)  # FPS 가져오기
        print(f"Original FPS: {fps}")

        max_distance = -10000
        max_distance_frame = None
        max_distance_keypoints = None
        
        frame_idx = 0
        avg_distance_last_4 = 0
        frames = []
        distance_list = []
        while cap.isOpened():
            ret, frame = cap.read()
            if not ret:
                break

            # frame = cv2.resize(frame, (640, 640))  # 프레임 크기 조정
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)  # RGB 변환

            # TensorFlow 텐서 변환
            image = tf.convert_to_tensor(frame, dtype=tf.uint8)
            image_height, image_width, channels = image.shape
            crop_region = init_crop_region(image_height, image_width)
            keypoints_with_scores = run_inference(movenet, image, crop_region, crop_size=[input_size, input_size])

            # Keypoint 데이터 처리
            keypoint_xy, _ = draw_prediction_on_image(
                image.numpy().astype(np.int32),
                keypoints_with_scores, crop_region=None,
                close_figure=True, output_image_height=300
            )
            print(keypoint_xy.shape)
            # 🔥 17개의 keypoint가 있는 경우만 처리
            if keypoint_xy is not None and keypoint_xy.shape[0] == 17:
                # 🔥 왼쪽/오른쪽 발목 위치
                
                left_foot = np.array(keypoint_xy[16][0])
                right_foot = np.array(keypoint_xy[15][0])
                foot = np.array([left_foot, right_foot])
                min_foot = np.minimum(left_foot, right_foot)
                shoulder =  np.array([keypoint_xy[5][0], keypoint_xy[6][0]])
                shoulder_avg = np.average(shoulder)
                ankle_left = np.array([keypoint_xy[15][0], keypoint_xy[15][1]])  # Keypoint 15 (왼쪽 발목)
                ankle_right = np.array([keypoint_xy[16][0], keypoint_xy[16][1]])  # Keypoint 16 (오른쪽 발목)
                # distance = np.linalg.norm(ankle_left - ankle_right)  # 두 발목 거리 계산
                distance = abs(left_foot - right_foot)
                distance_list.append(distance)
                if len(distance_list) > 6: distance_list.pop(0)  # 가장 오래된 값 제거

                # 🔥 4개 이상 쌓였을 때 평균 계산
                if len(distance_list) == 6: avg_distance_last_4 = np.mean(distance_list)
                print(f"{frame_idx} 번째 프레임에서 발 간격 평균 : {avg_distance_last_4}")

                cv2.putText(frame, str(f"{frame_idx} : {distance}"), (0, 50), cv2.FONT_HERSHEY_COMPLEX, 0.7, (0, 255, 255), 2)
                frames.append(frame)

                # 🔥 현재 프레임이 가장 큰 거리라면 갱신
                if avg_distance_last_4 > max_distance:
                    max_distance = avg_distance_last_4
                    max_distance_frame = frame  # 가장 넓은 프레임 저장
                    max_distance_keypoints = keypoint_xy #[coord for keypoint in keypoint_xy for coord in keypoint]  # 34개 값 저장

            frame_idx += 1

        cap.release()
        # 🔥 스켈레톤 데이터가 없으면 스킵
        if max_distance_frame is None or max_distance_keypoints is None:
            print(f"No valid keypoint data for {video_file}. Skipping...")
            continue

        print(f"가장 넓은 발 간격 프레임: {max_distance}")

        # ✅ **이미지 저장**
        release_image_folder = os.path.join(output_folder, "release_images")
        os.makedirs(release_image_folder, exist_ok=True)
        # to_mp4(frames, fps=30, input_file_path="C:\\Users\\SSAFY\\code\\PJT\\S12P11B202\\AI\\posture_correct\\release_images\\test.mp4", output_folder=output_folder)
        frame_bgr = cv2.cvtColor(max_distance_frame, cv2.COLOR_RGB2BGR)  # 다시 BGR 변환 (OpenCV 저장용)
        frame_path = os.path.join(release_image_folder, f"video{video_idx}.jpg")
        cv2.imwrite(frame_path, frame_bgr)  # 이미지 저장
        print(f"✅ 저장된 이미지: {frame_path}")

        # ✅ **JSON 저장**
        dataset = []
        dataset.append(max_distance_keypoints)
        save_to_json(np.array(dataset))
        video_idx += 1
    return max_distance_keypoints




def release_capture_for_infer(skel_list, fps=30):
        
        max_distance = -10000
        max_distance_keypoints = None

        frame_idx = 0
        avg_distance_last_4 = 0
        distance_list = []
        for skel in skel_list:
                frame_idx += 1
                # 🔥 왼쪽/오른쪽 발목 위치
                left_foot = np.array(skel[32])
                right_foot = np.array(skel[30])
                distance = abs(left_foot - right_foot)
                distance_list.append(distance)
                if len(distance_list) > 5: distance_list.pop(0)  # 가장 오래된 값 제거

                # 🔥 4개 이상 쌓였을 때 평균 계산
                if len(distance_list) == 5: avg_distance_last_4 = np.mean(distance_list)

                print(f"{frame_idx} 번째 프레임에서 발 거리: {distance}")

                # 🔥 현재 프레임이 가장 큰 거리라면 갱신
                if avg_distance_last_4 > max_distance:
                    max_distance = avg_distance_last_4
                    max_distance_keypoints = skel #[coord for keypoint in keypoint_xy for coord in keypoint]  # 34개 값 저장
            
        
        return max_distance_keypoints

def to_mp4(images, fps, input_file_path, output_folder):
    print("IN to_mp4 --- ")
    print(images[0].shape)
    height, width, _ = images[0].shape
    fourcc = cv2.VideoWriter_fourcc(*'mp4v')  # mp4v 코덱 사용
    input_file_name = os.path.basename(input_file_path)  # 입력 파일 이름 (경로 제외)
    output_file = os.path.join(output_folder, input_file_name)  # 출력 폴더에 동일한 파일 이름 사용
    video_writer = cv2.VideoWriter(output_file, fourcc, fps, (width, height))

    for image in images:
        # 이미지 데이터를 BGR로 변환 (OpenCV는 BGR 포맷을 사용)
        bgr_image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
        video_writer.write(bgr_image)

    video_writer.release()
    print(f"Video saved as {output_file}")