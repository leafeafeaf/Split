import os
from datetime import datetime
import boto3

#--- ① 원본 파일 경로 및 새 파일 이름 생성 ---
# 원본 동영상 파일 경로 (실제 존재하는 파일 경로 지정)
original_file_path = '/home/b202/Downloads/minion.mp4'

# 시리얼 번호 설정 (필요하면 동적으로 생성하거나 고정값 사용)
serial_number = '001'

# 현재 날짜와 시간을 원하는 형식(예: YYYYMMDD_HHMMSS)으로 가져와서 새 파일 이름 생성
current_time = datetime.now().strftime('%Y%m%d_%H%M%S')
new_file_name = f'minion_{serial_number}_{current_time}.mp4'

# 원본 파일이 있는 디렉토리와 조합하여 새 파일 경로 생성
directory = os.path.dirname(original_file_path)
new_file_path = os.path.join(directory, new_file_name)

#--- ② 파일 이름 변경 ---
if os.path.exists(original_file_path):
    os.rename(original_file_path, new_file_path)
    print(f"파일 이름이 변경되었습니다: {new_file_path}")
else:
    print(f"원본 파일을 찾을 수 없습니다: {original_file_path}")
    exit(1)

#--- ③ boto3 이용 S3 업로드 ---
# S3 버킷 이름과 S3 내에 저장될 객체 키(파일 이름)를 지정합니다.
bucket_name = 'tongsin-jetson-s3-test'
object_key = new_file_name  # S3에 업로드 시 파일 이름

# boto3 S3 클라이언트 생성 (자격 증명은 ~/.aws/credentials 또는 환경 변수에서 읽어옴)
s3_client = boto3.client('s3')

try:
    # upload_file(로컬 파일 경로, 버킷 이름, S3 객체 키) 메서드로 업로드
    s3_client.upload_file(new_file_path, bucket_name, object_key)
    print(f"파일 업로드 성공: {bucket_name}/{object_key}")
except Exception as e:
    print(f"S3 업로드 실패: {e}")