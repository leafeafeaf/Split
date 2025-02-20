import subprocess
import time
import os

class MainController:
def **init**(self):
# 각 모듈의 실행 파일 및 스크립트 경로 설정
self.esp32_comm_exe = "./esp32_comm"             # ESP32 데이터 수집 (C++ 실행 파일)
self.ai_main_script = "[AImain.py](http://aimain.py/)"                  # AI 분석 메인 (Python 스크립트)
self.data_analysis_exe = "./data_analysis"         # 센서 데이터 분석 (C++ 실행 파일)
self.http_request_exe = "./http_request"           # HTTP 전송 (C++ 실행 파일)
self.s3_upload_script = "s3_upload.py"              # S3 업로드 (Python 스크립트)
self.sensor_data_path = "data.json"                # 센서 데이터 파일 (ESP32에서 생성)
self.analysis_result_path = "analysis_result.json"  # 분석 결과 파일 (data_analysis 결과)
self.stop_signal_file = "stop_signal.txt"          # ESP32 정지 신호 파일
self.avgscore_sender_exe = "./avgscore_to_esp32"   # avgScore 전송 파일(C++ 실행 파일)
# ffmpeg 녹화를 위한 설정
self.video_width = 1920
self.video_height = 1080
self.video_fps = 20
self.video_output_file = "/home/b202/Downloads/input.mp4"
self.ffmpeg_process = None

def run_subprocess(self, command, timeout=30):
    try:
        if timeout is None:
            result = subprocess.run(command, capture_output=True, text=True)
        else:
            result = subprocess.run(command, capture_output=True, text=True, timeout=timeout)
        print(f"[INFO] 명령 실행 완료: {' '.join(command)}")
        print(f"[INFO] stdout: {result.stdout}")
        print(f"[INFO] stderr: {result.stderr}")
        if result.returncode != 0:
            print(f"[ERROR] 명령 실패 (반환 코드: {result.returncode})")
            raise subprocess.CalledProcessError(result.returncode, command)
    except subprocess.TimeoutExpired:
        print(f"[ERROR] 명령 시간 초과: {' '.join(command)}")
        raise
    except subprocess.CalledProcessError as e:
        print(f"[ERROR] 명령 실패: {e}")
        raise

def start_measurement(self):
    print("[1단계] 측정 시작: ESP32 데이터 수집 및 동영상 녹화 시작")
    if os.path.exists(self.video_output_file):
        os.remove(self.video_output_file)
        print("파일이 삭제되었습니다.")
    else:
        print("파일이 존재하지 않습니다.")
    # ffmpeg를 이용한 동영상 녹화 시작:
    # ffmpeg 명령어 예시: 성공적으로 /dev/video0에서 20fps, 1920x1080 해상도로 영상을 /home/b202/Downloads/input.mp4에 저장함.
    ffmpeg_command = [
        "ffmpeg",
        "-f", "v4l2",
        "-framerate", str(self.video_fps),
        "-video_size", f"{self.video_width}x{self.video_height}",
        "-i", "/dev/video0",
        "-y",
        self.video_output_file
        ]
    if (os.path.exists(self.video_output_file)): print("file generated")
    # ffmpeg를 백그라운드 프로세스로 실행
    self.ffmpeg_process = subprocess.Popen(ffmpeg_command, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    print("[INFO] ffmpeg 동영상 녹화 프로세스가 시작되었습니다.")

    # ESP32 데이터 수집 시작 ("start" 인자 전달)
    self.run_subprocess([self.esp32_comm_exe, "start"])
    print("측정이 진행 중입니다...")

def stop_measurement(self):
    print("[2단계] 측정 종료: 데이터 수집 정지 및 동영상 녹화 중지")
    # ESP32 데이터 수집 종료를 위한 stop_signal 파일 생성
    try:
        with open(self.stop_signal_file, "w") as f:
            f.write("STOP")
        print("[INFO] stop_signal 파일을 생성하였습니다.")
    except Exception as e:
        print(f"[ERROR] stop_signal 파일 생성 실패: {e}")
        raise

    # ESP32가 종료 신호를 감지할 시간을 기다림
    time.sleep(1)
    # ffmpeg 녹화 프로세스 종료: SIGINT를 보내어 깨끗하게 종료
    if self.ffmpeg_process is not None:
        self.ffmpeg_process.terminate()  # 또는 os.kill(self.ffmpeg_process.pid, signal.SIGINT)
        self.ffmpeg_process.wait()
        print("[INFO] ffmpeg 녹화 프로세스가 종료되었습니다.")

def run_ai_analysis(self):
    print("[3단계] AI 분석 실행 (AImain.py 실행)")
    self.run_subprocess(["python3", self.ai_main_script], timeout=None)
    print("AI 분석 완료.")

def run_analysis(self):
    print("[4단계] 데이터 분석 실행 (data_analysis 실행)")
    self.run_subprocess([self.data_analysis_exe, self.sensor_data_path, self.analysis_result_path])
    print(f"데이터 분석 완료. 결과는 {self.analysis_result_path}에 저장되었습니다.")

def send_http(self):
    print("[5단계] HTTP 요청 전송 (http_request 실행)")
    self.run_subprocess([self.http_request_exe, self.analysis_result_path])
    print("HTTP 전송 완료.")

def upload_s3(self):
    print("[6단계] S3 업로드 진행 (s3_upload 실행)")
    self.run_subprocess(["python3", self.s3_upload_script, self.analysis_result_path])
    print("S3 업로드 완료.")

def send_avgscore(self):
    print("[7단계] avgScore 전송 (avgscore_to_esp32 실행)")
    self.run_subprocess([self.avgscore_sender_exe])
    print("avgScore 전송 완료.")

def run(self):
    print(f"[DEBUG] 현재 작업 디렉토리: {os.getcwd()}")
    try:
        print("전체 시스템 자동 실행을 시작합니다.")
        self.start_measurement()      # 1단계: 측정 시작 (데이터 수집 및 동영상 녹화 시작)
        self.stop_measurement()       # 2단계: 측정 종료 (stop 신호 및 ffmpeg 종료)
        self.run_ai_analysis()        # 3단계: AI 분석
        self.run_analysis()           # 4단계: 데이터 분석
        self.send_http()              # 5단계: HTTP 전송
        self.upload_s3()              # 6단계: S3 업로드
        self.send_avgscore()          # 7단계: avgScore 전송

        print("모든 단계가 성공적으로 완료되었습니다.")
        print(f"[INFO] 녹화된 동영상 파일 경로: {self.video_output_file}")
    except Exception as e:
        print(f"[ERROR] 워크플로우 중 오류 발생: {e}")
        print("프로그램을 종료합니다.")
        
        
if name == "main":
controller = MainController()
controller.run()
