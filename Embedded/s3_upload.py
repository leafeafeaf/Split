import os
from datetime import datetime
import boto3
import sys
import subprocess
import requests

def convert_to_streaming_mp4(input_file_path):
    """영상을 스트리밍 가능한 MP4로 변환"""
    output_file_path = input_file_path.replace('.mp4', '_streaming.mp4')
    try:
        command = [
            'ffmpeg',
            '-i', input_file_path,
            '-movflags', 'faststart',
            '-c:v', 'libx264',
            '-preset', 'fast',
            '-crf', '23',
            '-c:a', 'aac',
            '-b:a', '128k',
            output_file_path
        ]
        subprocess.run(command, check=True)
        print(f"영상 변환 완료: {output_file_path}")
        return output_file_path
    except subprocess.CalledProcessError as e:
        print(f"영상 변환 실패: {e}")
        sys.exit(1)

def read_num_from_file():
    try:
        with open("num.txt", "r") as f:
            return f.read().strip()
    except Exception as e:
        print(f"num.txt 읽기 실패: {e}")
        return None

def rename_file(original_file_path):
    if not os.path.exists(original_file_path):
        print(f"원본 파일을 찾을 수 없습니다: {original_file_path}")
        sys.exit(1)
    
    num = read_num_from_file()
    if not num:
        print("num 값을 찾을 수 없습니다.")
        sys.exit(1)
    
    current_time = datetime.now().strftime('%y%m%d%H%M%S')
    new_file_name = f"frame{num}-{current_time}.mp4"
    directory = os.path.dirname(original_file_path)
    new_file_path = os.path.join(directory, new_file_name)
    
    try:
        os.rename(original_file_path, new_file_path)
        print(f"파일 이름이 변경되었습니다: {new_file_path}")
    except Exception as e:
        print(f"파일 이름 변경 실패: {e}")
        sys.exit(1)
    return new_file_path, new_file_name

def upload_to_s3(file_path, bucket_name, object_key):
    s3_client = boto3.client('s3')
    try:
        s3_client.upload_file(
            file_path, 
            bucket_name, 
            object_key, 
            ExtraArgs={
                'Tagging': 'expire=1day',
                'ContentType': 'video/mp4'
            }
        )
        print(f"파일 업로드 성공: {bucket_name}/{object_key}")
        return True
    except Exception as e:
        print(f"S3 업로드 실패: {e}")
        sys.exit(1)

def send_video_url_to_backend(video_url):
    num = read_num_from_file()  # num 값을 읽어옴
    if not num:
        print("num 값을 찾을 수 없습니다.")
        return False
    
    url = f"https://i12b202.p.ssafy.io/api/device/1/frame/{num}/video"  # f-string으로 num 값 삽입
    payload = {"video": video_url}
    try:
        response = requests.post(url, json=payload)
        print(f"비디오 URL 전송 결과: {response.status_code}")
        return response.status_code == 200
    except Exception as e:
        print(f"비디오 URL 전송 실패: {e}")
        return False

if __name__ == '__main__':
    current_dir = os.path.dirname(os.path.abspath(__file__))
    video_file_path = os.path.join(current_dir, "input.mp4")
    
    if not os.path.exists(video_file_path):
        print(f"동영상 파일을 찾을 수 없습니다: {video_file_path}")
        sys.exit(1)
    
    # 스트리밍 가능한 MP4로 변환
    streaming_file_path = convert_to_streaming_mp4(video_file_path)
    
    bucket_name = "split-bucket-first-1"
    
    # 변환된 파일의 이름 변경
    new_file_path, new_file_name = rename_file(streaming_file_path)
    
    # S3에 업로드
    if upload_to_s3(new_file_path, bucket_name, new_file_name):
        # S3 URL 생성 및 백엔드로 전송
        video_url = f"https://{bucket_name}.s3.ap-northeast-2.amazonaws.com/{new_file_name}"
        send_video_url_to_backend(video_url)
    
    # 임시 파일 정리
    try:
        if os.path.exists(streaming_file_path) and streaming_file_path != new_file_path:
            os.remove(streaming_file_path)
    except Exception as e:
        print(f"임시 파일 삭제 실패: {e}")