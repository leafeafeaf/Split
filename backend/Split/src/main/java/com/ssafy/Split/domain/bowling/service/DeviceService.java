package com.ssafy.Split.domain.bowling.service;

import com.ssafy.Split.domain.bowling.domain.dto.request.DeviceMeasurementRequest;
import com.ssafy.Split.domain.bowling.domain.entity.Device;
import com.ssafy.Split.domain.bowling.domain.entity.Frame;
import com.ssafy.Split.domain.bowling.domain.entity.Progress;
import com.ssafy.Split.domain.bowling.exception.DeviceInUseException;
import com.ssafy.Split.domain.bowling.exception.DeviceNotFoundException;
import com.ssafy.Split.domain.bowling.repository.DeviceRepository;
import com.ssafy.Split.domain.bowling.repository.FrameRepository;
import com.ssafy.Split.domain.bowling.repository.ProgressRepository;
import com.ssafy.Split.domain.user.domain.dto.CustomUserDetails;
import com.ssafy.Split.domain.user.domain.entity.User;
import com.ssafy.Split.domain.user.exception.UserNotFoundException;
import com.ssafy.Split.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

  private final DeviceRepository deviceRepository;
  private final ProgressRepository progressRepository;
  private final FrameRepository frameRepository;
  private final UserRepository userRepository;

  @Transactional
  public void startMeasurement(int serial, DeviceMeasurementRequest request) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    int userId = userDetails.getUser().getId();

    // 디바이스 존재 여부 확인
    Device device = deviceRepository.findBySerialNumber(serial)
        .orElseThrow(() -> new DeviceNotFoundException(
            "Device not found with serial: " + userId));

    // 디바이스 사용 중 여부 확인
    if (progressRepository.existsByDeviceSerialNumberAndStatusInProgress(
        userId)) {
      throw new DeviceInUseException();
    }

    // 사용자 존재 여부 확인
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    log.info("4");

    // Progress 생성
    Progress progress = Progress.builder()
        .device(device)
        .user(user)
        .frameCount(0)
        .time(LocalDateTime.now())
        .build();

    log.info("progress : {}", progress);
    Progress savedProgress = progressRepository.save(progress);
    log.info("savedProgress : {}", savedProgress);

    // Frame 생성
    Frame frame = Frame.builder()
        .progress(savedProgress)
        .device(device)
        .num(1)  // 첫 번째 프레임
        .build();
    log.info("frame : {}", frame);
    frameRepository.save(frame);
  }
}
