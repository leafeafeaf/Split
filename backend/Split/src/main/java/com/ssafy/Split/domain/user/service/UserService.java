package com.ssafy.Split.domain.user.service;

import com.ssafy.Split.domain.user.domain.dto.request.SignupRequestDto;
import com.ssafy.Split.domain.user.domain.dto.request.UpdateUserRequestDto;
import com.ssafy.Split.domain.user.domain.dto.response.UserInfoResponseDto;
import com.ssafy.Split.domain.user.domain.entity.User;
import com.ssafy.Split.domain.user.repository.UserRepository;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import com.ssafy.Split.global.infra.s3.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final S3Service s3Service;
  private final BCryptPasswordEncoder passwordEncoder;


  public void deleteHighlight(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND));

    String highlightUrl = user.getHighlight();
    if (highlightUrl != null && !highlightUrl.isEmpty()) {
      // S3에서 파일 삭제
      s3Service.deleteFile(highlightUrl);

      // DB에서 하이라이트 URL 제거
      user.updateHighlight(null);
      userRepository.save(user);

      log.info("Highlight deleted for user: {}", userId);

    }
  }

  public void createHighlight(Integer userId, String highlight) {
    // URL 형식 검증
    if (!isValidVideoUrl(highlight)) {
      throw new SplitException(ErrorCode.INVALID_VIDEO_URL);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND, String.valueOf(userId)));

    // 하이라이트가 이미 존재하는 경우
    if (user.getHighlight() != null && !user.getHighlight().isEmpty()) {
      throw new SplitException(ErrorCode.HIGHLIGHT_ALREADY_EXISTS);
    }

    user.createHighlight(highlight);
    userRepository.save(user);

    log.info("Highlight created for user {}: {}", userId, highlight);


  }

  public void updateHighlight(Integer userId, String highlight) {
    // URL 형식 검증
    if (!isValidVideoUrl(highlight)) {
      throw new SplitException(ErrorCode.INVALID_VIDEO_URL);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND, String.valueOf(userId)));

    // 기존 하이라이트가 없는 경우
    if (user.getHighlight() == null || user.getHighlight().isEmpty()) {
      throw new SplitException(ErrorCode.HIGHLIGHT_NOT_FOUND);
    }

    user.updateHighlight(highlight);
    userRepository.save(user);

    log.info("Highlight updated for user {}: {}", userId, highlight);
  }

  /**
   * 테마 변경
   **/
  public void updateThema(Integer userId, Integer thema) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND, String.valueOf(userId)));

    user.updateThema(thema);
    log.info("User {} thema updated to {}", userId, thema);
  }

  private boolean isValidVideoUrl(String url) {
    return url != null &&
        url.startsWith("https://split-bucket-first-1.s3.ap-northeast-2.amazonaws.com/") &&
        (url.endsWith(".mov") || url.endsWith(".mp4"));
  }

  public void signupUser(@Valid SignupRequestDto signupRequest) {

    if (userRepository.existsByEmail(signupRequest.getEmail())) {
      throw new SplitException(ErrorCode.USER_ALREADY_EXISTS, "email", signupRequest.getEmail());
    }
    if (userRepository.existsByNickname(signupRequest.getNickname())) {
      throw new SplitException(ErrorCode.USER_ALREADY_EXISTS, "nickname",
          signupRequest.getNickname());
    }

    // 2️⃣ 비밀번호 암호화
    String encryptedPassword = passwordEncoder.encode(signupRequest.getPassword());

    // 3️⃣ 유저 엔티티 생성
    User user = User.builder()
        .email(signupRequest.getEmail())
        .password(encryptedPassword) // 🔒 암호화된 비밀번호 저장
        .nickname(signupRequest.getNickname())
        .gender(signupRequest.getGender())
        .height(signupRequest.getHeight()) // 선택 입력 (null 가능)
        .build();

    // 4️⃣ 유저 저장
    userRepository.save(user);
  }

  public void checkNickname(String nickname) {
    //TODO 새롭게 정해지는 닉네임 규칙에 따라 커스텀
    if (nickname == null || nickname.isEmpty()) {
      throw new SplitException(ErrorCode.INVALID_INPUT_VALUE, "nickname");
    }
    if (userRepository.existsByNickname(nickname)) {
      throw new SplitException(ErrorCode.USER_ALREADY_EXISTS, "nickname", nickname);
    }
  }

  public UserInfoResponseDto.UserData getUserInfo(int id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND, String.valueOf(id)));

    return UserInfoResponseDto.UserData.builder()
        .id(user.getId())
        .email(user.getEmail())
        .gender(user.getGender())
        .height(user.getHeight())
        .nickname(user.getNickname())
        .totalGameCount(user.getTotalGameCount())
        .highlight(user.getHighlight())
        .totalPoseHighscore(
            user.getTotalPoseHighscore() != null ? user.getTotalPoseHighscore().doubleValue() : 0.0)
        .totalPoseAvgscore(
            user.getTotalPoseAvgscore() != null ? user.getTotalPoseAvgscore().doubleValue() : 0.0)
        .elbowAngleScore(
            user.getElbowAngleScore() != null ? user.getElbowAngleScore().doubleValue() : 0.0)
        .armStabilityScore(
            user.getArmStabilityScore() != null ? user.getArmStabilityScore().doubleValue() : 0.0)
        .armSpeedScore(
            user.getArmSpeedScore() != null ? user.getArmSpeedScore().doubleValue() : 0.0)
        .thema(user.getThema())
        .currBowlingScore(user.getCurrBowlingScore())
        .avgBowlingScore(user.getAvgBowlingScore())
        .build();

  }

  public void deleteUser(int id) {
    if (!userRepository.existsById(id)) {
      throw new SplitException(ErrorCode.USER_NOT_FOUND, String.valueOf(id));
    }

    userRepository.deleteById(id);
  }

  public void updateUser(User user, @Valid UpdateUserRequestDto updateRequest) {
    int id = user.getId();
    user = userRepository.findById(id)
        .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND, String.valueOf(id)));

    String nickname = updateRequest.getNickname();
    //이름이 변경되었다면 중복확인
    if (!nickname.equals(user.getNickname())) {
      if (userRepository.existsByNickname(nickname)) {
        throw new SplitException(ErrorCode.USER_ALREADY_EXISTS, "nickname", nickname);
      }
    }

    user.updateUser(user, updateRequest);

    userRepository.save(user); // 변경사항 저장

  }

}

