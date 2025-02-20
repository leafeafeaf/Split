import cv2
import time

def record_video(output_file="/home/b202/Downloads/input.mp4", duration=10):
    """
    지정한 duration(초) 동안 웹캠으로부터 영상을 촬영하여 output_file 경로에 저장합니다.
    
    Parameters:
        output_file (str): 저장할 파일 경로 (기본: /home/b202/Downloads/input.mp4)
        duration (float): 녹화할 시간(초)
    """
    # 웹캠 열기 (device index 0, 즉 /dev/video0)
    cap = cv2.VideoCapture(0)
    if not cap.isOpened():
        print("Error: 웹캠을 열 수 없습니다.")
        return

    # VideoWriter 설정 (코덱: mp4v 사용)
    fourcc = cv2.VideoWriter_fourcc(*'mp4v')
    fps = 20.0
    width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    out = cv2.VideoWriter(output_file, fourcc, fps, (width, height))

    start_time = time.time()
    print(f"녹화를 시작합니다. (총 녹화 시간: {duration}초)")

    # 지정한 duration 동안 녹화
    while time.time() - start_time < duration:
        ret, frame = cap.read()
        if not ret:
            print("Error: 프레임을 읽을 수 없습니다.")
            break
        out.write(frame)

    cap.release()
    out.release()
    print(f"녹화 종료. 동영상이 저장되었습니다: {output_file}")

if __name__ == "__main__":
    # 예제: 10초 동안 녹화
    record_video(duration=10)
