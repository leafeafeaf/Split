package com.ssafy.Split.bowling.service;

import com.ssafy.Split.bowling.controller.AuthenticationProvider;
import com.ssafy.Split.bowling.domain.dto.request.DeviceMeasurementRequest;
import com.ssafy.Split.bowling.domain.entity.Device;
import com.ssafy.Split.bowling.domain.entity.Frame;
import com.ssafy.Split.bowling.domain.entity.Progress;
import com.ssafy.Split.bowling.exception.DeviceInUseException;
import com.ssafy.Split.bowling.exception.DeviceNotFoundException;
import com.ssafy.Split.bowling.repository.DeviceRepository;
import com.ssafy.Split.bowling.repository.FrameRepository;
import com.ssafy.Split.bowling.repository.ProgressRepository;
import com.ssafy.Split.user.domain.entity.User;
import com.ssafy.Split.user.exception.UserNotFoundException;
import com.ssafy.Split.user.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Builder
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final ProgressRepository progressRepository;
    private final FrameRepository frameRepository;
    private final AuthenticationProvider authProvider;
    private final UserRepository userRepository;

    @Transactional
    public void startMeasurement(String serial, DeviceMeasurementRequest request, String token) {
        // 토큰 검증 (현재는 항상 true)
//        if (!authProvider.validateToken(token)) {
//            throw new InvalidTokenException();

        // 토큰에서 사용자 ID 추출 (현재는 request의 userID 사용)
        Long tokenUserId = authProvider.getUserIdFromToken(token);
        Long userId = tokenUserId != null ? tokenUserId : request.getUserId();

        // 디바이스 존재 여부 확인
        Device device = deviceRepository.findBySerialNumber(request.getSerialNumber())
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with serial: " + request.getSerialNumber()));

        // 디바이스 사용 중 여부 확인
        if (progressRepository.existsByDeviceSerialNumberAndStatusInProgress(request.getSerialNumber())) {
            throw new DeviceInUseException();
        }
        // 사용자 존재 여부 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getUserId()));



        // Progress 생성
        Progress progress = Progress.builder()
                .device(device)
                .user(user)
                .frameCount(0)
                .time(LocalDateTime.now())
                .build();
        log.info("progress : {}", progress);


        Progress savedProgress = progressRepository.save(progress);
        log.info("savedProgress : {}",savedProgress);

        // Frame 생성
        Frame frame = Frame.builder()
                .progress(savedProgress)
                .device(device)
                .num(1)  // 첫 번째 프레임
//                .video(null)  // 초기에는 비디오 없음
                .build();
        log.info("frame : {}" ,frame);
        frameRepository.save(frame);
        }
}
