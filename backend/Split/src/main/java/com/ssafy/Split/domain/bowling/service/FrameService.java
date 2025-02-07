package com.ssafy.Split.domain.bowling.service;


import com.ssafy.Split.domain.bowling.domain.dto.request.FrameUploadRequest;
import com.ssafy.Split.domain.bowling.domain.dto.request.VideoUploadRequest;
import com.ssafy.Split.domain.bowling.domain.entity.Device;
import com.ssafy.Split.domain.bowling.domain.entity.Frame;
import com.ssafy.Split.domain.bowling.domain.entity.Progress;
import com.ssafy.Split.domain.bowling.exception.DeviceNotFoundException;
import com.ssafy.Split.domain.bowling.exception.FrameNotFoundException;
import com.ssafy.Split.domain.bowling.exception.ProgressNotFoundException;
import com.ssafy.Split.domain.bowling.exception.InvalidVideoUrlException;
import com.ssafy.Split.domain.bowling.repository.DeviceRepository;
import com.ssafy.Split.domain.bowling.repository.FrameRepository;
import com.ssafy.Split.domain.bowling.repository.ProgressRepository;
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
    public Integer uploadFrame(String serial, FrameUploadRequest request) {

        // Device 조회
        Device device = deviceRepository.findBySerialNumber(Integer.valueOf(serial))
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with serial: " + serial));

        // 진행 중인 Progress 조회
        Progress progress = progressRepository.findByDeviceSerialNumber(Integer.valueOf(serial))
                .orElseThrow(() -> new ProgressNotFoundException("No active progress found for device: " + serial));

        // 현재 프레임 번호 계산
        Integer currentFrameNum = frameRepository.countByProgressId(progress.getId()) + 1;

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

    public void uploadVideo(String serial, Integer frameNum, VideoUploadRequest request) {
        // Device 조회
        Device device = deviceRepository.findBySerialNumber(Integer.valueOf(serial))
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with serial: " + serial));

        // 진행 중인 Progress 조회
        Progress progress = progressRepository.findByDeviceSerialNumber(Integer.valueOf(serial))
                .orElseThrow(() -> new ProgressNotFoundException("No active progress found for device: " + serial));

        // Frame 조회
        Frame frame = frameRepository.findByProgressIdAndNum(progress.getId(), frameNum)
                .orElseThrow(() -> new FrameNotFoundException("Frame not found: " + frameNum));

        // 비디오 URL 업데이트
        frame.updateVideo(request.getVideo());
        frameRepository.save(frame);

        // 비디오 URL이 이미 존재하는지 체크
        if (frame.getVideo() == null && frame.getVideo().trim().isEmpty()) {
            throw new InvalidVideoUrlException("Video URL already exists");
        }


        log.info("Video URL updated for frame {} of device {}: {}", frameNum, serial, request.getVideo());
    }
}
