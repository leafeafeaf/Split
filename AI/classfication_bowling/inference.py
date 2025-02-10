from tqdm import tqdm
import torch 
import cv2
from preprocessing.video_preprocess import *
from training.input import InputData
from torch.utils.data import DataLoader
from config import device 

def infer():
    from training.visualization import net
    net.load_state_dict(torch.load('best_1330.pth'))
    net.eval()
    out_img_list = []
    dataset = []
    status = 'None'

    print('시퀀스 데이터 분석 중...')
    xy_list_list = []
    test_video_path = 'C:\\Users\\SSAFY\\Documents\\oCam\\seongmin_resize.mp4'  # 테스트용 영상
    output_folder = 'C:\\Users\\SSAFY\\Desktop\\infer_out\\LSTM_Modified'
    bowling_folder = 'C:\\Users\\SSAFY\\Desktop\\infer_out\\LSTM_Modified\\bowling'
    (skel_dataset, img_list, bowling_start) = process_video_infer(test_video_path, output_folder)


    # 영상 이미지별 skeleton 리스트
    # 영상 이미지별 skeleton까지 입힌 이미지 리스트

    test_list = []
    bowling_end = 0
    bowling_img_list = []
    bowling_skel_list = []
    for idx in tqdm(range(len(skel_dataset))):
        print(f"현재 idx: {idx}")    
        # length = 10으로 해보고 안되면 20
        length = 7
        
        test_list.append(skel_dataset[idx])
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
        cv2.putText(img_list[idx], status, (0, 50), cv2.FONT_HERSHEY_COMPLEX, 1.5, (0, 0, 255), 2)
        # cv2.imshow(img_list[idx])
        out_img_list.append(img_list[idx])

    for i in range(bowling_start, bowling_end + 1):
        bowling_img_list.append(img_list[i])
        bowling_skel_list.append(skel_dataset[i])
    print(len(bowling_img_list))
    to_mp4(out_img_list, fps=30, input_file_path=test_video_path, output_folder=output_folder)
    to_mp4(bowling_img_list, fps=30, input_file_path=test_video_path, output_folder=bowling_folder)