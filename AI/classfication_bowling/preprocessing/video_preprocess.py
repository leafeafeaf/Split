from preprocessing.skeleton import *
import cv2
import tensorflow as tf
import numpy as np
import os
from preprocessing.labeling import labeling

def process_videos(input_folder, output_folder, fps=30):
    cnt = 1
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    video_files = [f for f in os.listdir(input_folder) if f.endswith('.mp4')]
    video_files.sort(key=lambda f: int(''.join(filter(str.isdigit, f))))
    skel_dataset = []
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

        # 비디오의 모든 프레임을 읽기
        while cap.isOpened():
            ret, frame = cap.read()
            if not ret:
                break
            # BGR -> RGB 변환 (OpenCV는 기본적으로 BGR로 이미지를 읽음)
            frame = cv2.resize(frame,(640,640))
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            frames.append(frame)

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
        print(f"Processing video: {video_file}")
        print(f"Number of frames: {num_frames}, Image height: {image_height}, Image width: {image_width}, Channels: {channels}")

        # Crop 영역 설정 (여기서 사용하는 함수들을 정의해두셔야 합니다)
        crop_region = init_crop_region(image_height, image_width)

        output_images = []

        # 한 동영상의 frame별 keypoint좌표를 저장할 리스트
        
        # bar = display(progress(0, num_frames-1), display_id=True)
        for frame_idx in range(num_frames):
            keypoint_dataset = [] # keypoint 좌표가 들어가는 배열
            # print(frame_idx)
            keypoints_with_scores = run_inference(
            movenet, image[frame_idx, :, :, :], crop_region,
            crop_size=[input_size, input_size])
            (keypoint_xy,output_images_input) =draw_prediction_on_image(
            image[frame_idx, :, :, :].numpy().astype(np.int32),
            keypoints_with_scores, crop_region=None,
            close_figure=True, output_image_height=300)
        
            # print(keypoint_xy) # 키 포인트 출력 
            if keypoint_xy is not None and keypoint_xy.shape[0] == 17:   # 17개 관절이 모두 있어야 함
                for keypoint_dict_idx in range(len(keypoint_xy)):
                    for keypoint_xy_idx in range(len(keypoint_xy[0])):
                        keypoint_dataset.append(keypoint_xy[keypoint_dict_idx][keypoint_xy_idx])
            if len(keypoint_dataset) == 34:
                skel_per_video.append(keypoint_dataset)
            output_images.append(output_images_input)
            crop_region = determine_crop_region(
            keypoints_with_scores, image_height, image_width)
            # bar.update(progress(frame_idx, num_frames-1))
        #   output_images.append(draw_prediction_on_image(
        #       image[frame_idx, :, :, :].numpy().astype(np.int32),
        #       keypoints_with_scores, crop_region=None,
        #       close_figure=True, output_image_height=300))
        output = np.stack(output_images, axis=0)
        print(f"skel_per_video : {skel_per_video}")
        print(f"skel_per_video_len : {len(skel_per_video)}")

        labeling(video_file,skel_per_video)

        # skel_dataset.append(skel_per_video)

    # return skel_dataset

        # 결과를 MP4로 저장
        # to_mp4(output_images, fps=fps, input_file_path=video_path, output_folder=output_folder)


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

    # 비디오의 모든 프레임을 읽기
    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break
        # BGR -> RGB 변환 (OpenCV는 기본적으로 BGR로 이미지를 읽음)
        frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        frames.append(frame)

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
    skel_dataset = []

    for frame_idx in range(num_frames):
        keypoint_dataset = []

        # 키포인트 추론 수행
        keypoints_with_scores = run_inference(
            movenet, image[frame_idx, :, :, :], crop_region,
            crop_size=[input_size, input_size]
        )

        # 키포인트 시각화 및 데이터 추출
        keypoint_xy, output_image = draw_prediction_on_image(
            image[frame_idx, :, :, :].numpy().astype(np.int32),
            keypoints_with_scores, crop_region=None,
            close_figure=True, output_image_height=300
        )

        # 키포인트 데이터 저장
        if keypoint_xy is not None and keypoint_xy.shape[0] == 17: 
            for keypoint_dict_idx in range(len(keypoint_xy)):
                for keypoint_xy_idx in range(len(keypoint_xy[0])):
                    keypoint_dataset.append(keypoint_xy[keypoint_dict_idx][keypoint_xy_idx])
                    
        if len(keypoint_dataset) == 34:
            skel_dataset.append(keypoint_dataset)
        output_images.append(output_image)

        # 새로운 crop_region 업데이트
        crop_region = determine_crop_region(
            keypoints_with_scores, image_height, image_width
        )

    
    return skel_dataset, output_images

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