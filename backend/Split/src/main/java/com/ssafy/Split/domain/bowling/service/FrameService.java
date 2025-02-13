package com.ssafy.Split.domain.bowling.service;


import com.ssafy.Split.domain.bowling.domain.dto.request.FrameUploadRequest;
import com.ssafy.Split.domain.bowling.domain.dto.request.VideoUploadRequest;
import com.ssafy.Split.domain.bowling.domain.dto.response.FrameListResponse.FrameData;
import com.ssafy.Split.domain.bowling.domain.entity.Device;
import com.ssafy.Split.domain.bowling.domain.entity.Frame;
import com.ssafy.Split.domain.bowling.domain.entity.Progress;
import com.ssafy.Split.domain.bowling.repository.DeviceRepository;
import com.ssafy.Split.domain.bowling.repository.FrameRepository;
import com.ssafy.Split.domain.bowling.repository.ProgressRepository;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FrameService {

  private final FrameRepository frameRepository;
  private final ProgressRepository progressRepository;
  private final DeviceRepository deviceRepository;

  @Transactional
  public Integer uploadFrame(int serial, FrameUploadRequest request) {

    // Device 조회
    Device device = deviceRepository.findBySerialNumber(serial)
        .orElseThrow(
            () -> new SplitException(ErrorCode.DEVICE_NOT_FOUND, String.valueOf(serial)));

    // 진행 중인 Progress 조회
    Progress progress = progressRepository.findByDeviceSerialNumber(serial)
        .orElseThrow(
            () -> new SplitException(ErrorCode.PROGRESS_NOT_FOUND, String.valueOf(serial)));

    if (progress.getFrameCount() >= 10) {
      throw new SplitException(ErrorCode.MAX_FRAME_LIMIT);
    }

    // 현재 프레임 번호 계산
    Integer currentFrameNum = progress.getFrameCount() + 1;

    // Frame 생성
    Frame frame = Frame.builder()
        .progress(progress)
        .device(device)
        .num(currentFrameNum)
        .isSkip(request.getIsSkip())
        .feedback(request.getFeedback())
        .poseSocre(request.getPoseScore())
        .elbowAngleScore(request.getElbowAngleScore())
        .armStabilityScore(request.getArmStabilityScore())
        .speed(request.getSpeed())
        .build();

    frameRepository.save(frame);

    // Progress의 frameCount 업데이트
    progress.updateFrameCount(currentFrameNum);
    progressRepository.save(progress);

    log.info("Frame uploaded for device {}: {}", serial, frame);

    return currentFrameNum;
  }

  public void uploadVideo(int serial, Integer frameNum, VideoUploadRequest request) {
//    // Device 조회
    deviceRepository.findBySerialNumber(serial)
        .orElseThrow(() -> new SplitException(ErrorCode.DEVICE_NOT_FOUND, String.valueOf(serial)));

    // 진행 중인 Progress 조회
    Progress progress = progressRepository.findByDeviceSerialNumber(serial)
        .orElseThrow(
            () -> new SplitException(ErrorCode.PROGRESS_NOT_FOUND, String.valueOf(serial)));

    // Frame 조회
    Frame frame = frameRepository.findByProgressIdAndNum(progress.getId(), frameNum)
        .orElseThrow(() -> new SplitException(ErrorCode.FRAME_NOT_FOUND, String.valueOf(frameNum)));

    //해당 프레임의 비디오 URL이 이상하다.
    if (!(isValidVideoUrl(request.getVideo()))) {
      log.info("#############{}", request.getVideo());
      throw new SplitException(ErrorCode.INVALID_VIDEO_URL);
    }

    // 비디오 URL이 이미 존재하는지 체크
    if (frame.getVideo() != null) {
      throw new SplitException(ErrorCode.VIDEO_ALREADY_EXISTS);
    }

    // 비디오 URL 업데이트
    frame.updateVideo(request.getVideo());
    frameRepository.save(frame);

    log.info("Video URL updated for frame {} of device {}: {}", frameNum, serial,
        request.getVideo());
  }

  private boolean isValidVideoUrl(String url) {
    return url.startsWith("https://split-bucket-first-1.s3.ap-northeast-2.amazonaws.com/") &&
        (url.endsWith(".mov") || url.endsWith(".mp4"));
  }

  /**
   * 프레임 조회
   **/
  public Frame getFrame(Integer serialNumber, Integer frameNum) {
    Progress progress = progressRepository.findByDeviceSerialNumber(serialNumber)
        .orElseThrow(
            () -> new SplitException(ErrorCode.PROGRESS_NOT_FOUND, String.valueOf(serialNumber)));
    return frameRepository.findByProgressIdAndNum(progress.getId(), frameNum)
        .orElseThrow(() -> new SplitException(ErrorCode.FRAME_NOT_FOUND, String.valueOf(frameNum)));
  }

  public List<FrameData> getAllFrames(Integer serialNumber) {
    Progress progress = progressRepository.findByDeviceSerialNumber(serialNumber)
        .orElseThrow(
            () -> new SplitException(ErrorCode.PROGRESS_NOT_FOUND, String.valueOf(serialNumber)));

    List<Frame> frames = frameRepository.findAllByProgressIdOrderByNumAsc(progress.getId());
    return frames.stream()
        .map(frame -> FrameData.builder()
            .id(frame.getId())
            .progressId(frame.getProgress().getId())
            .serialNumber(frame.getDevice().getSerialNumber())
            .num(frame.getNum())
            .video(frame.getVideo())
            .isSkip(frame.getIsSkip() ? 1 : 0)
            .feedback(frame.getFeedback())
            .poseScore(frame.getPoseSocre())
            .elbowAngleScore(frame.getElbowAngleScore())
            .armStabilityScore(frame.getArmStabilityScore())
            .speed(frame.getSpeed())
            .build())
        .collect(Collectors.toList());
  }
}
