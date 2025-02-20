package com.ssafy.Split.domain.bowling.service;

import com.ssafy.Split.domain.bowling.domain.entity.Device;
import com.ssafy.Split.domain.bowling.domain.entity.Progress;
import com.ssafy.Split.domain.bowling.repository.DeviceRepository;
import com.ssafy.Split.domain.bowling.repository.ProgressRepository;
import com.ssafy.Split.domain.user.domain.entity.User;
import com.ssafy.Split.domain.user.repository.UserRepository;
import com.ssafy.Split.global.common.JWT.domain.CustomUserDetails;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
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
  private final UserRepository userRepository;

  @Transactional
  public void startMeasurement(int serial) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    int userId = userDetails.getUser().getId();

    // 디바이스 존재 여부 확인
    Device device = deviceRepository.findBySerialNumber(serial)
        .orElseThrow(() -> new SplitException(ErrorCode.DEVICE_NOT_FOUND, String.valueOf(serial)));

    // 사용자 존재 여부 확인
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND, String.valueOf(userId)));

    // 디바이스의 Progress 확인
    LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
    Progress existingProgress = progressRepository.findByDeviceSerialNumber(serial)
        .orElse(null);

    if (existingProgress != null) {
      // 프로그레스가 존재하는 경우
      if (existingProgress.getTime().isAfter(oneHourAgo)) {
        // 1시간 이내의 프로그레스인 경우 - 사용 중 예외 발생
        throw new SplitException(ErrorCode.DEVICE_IN_USE, String.valueOf(serial));
      } else {
        // 1시간이 지난 프로그레스인 경우 - 기존 데이터 삭제 후 새로운 게임 시작
        progressRepository.delete(existingProgress);
      }
    }

    // 새로운 Progress 생성
    Progress progress = Progress.builder()
        .device(device)
        .user(user)
        .frameCount(0)
        .time(LocalDateTime.now())
        .build();

    progressRepository.save(progress);
  }
}
