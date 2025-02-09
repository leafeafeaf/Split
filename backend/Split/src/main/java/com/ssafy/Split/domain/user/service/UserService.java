package com.ssafy.Split.domain.user.service;

import com.ssafy.Split.domain.user.domain.dto.request.SignupRequestDto;
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
                .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND));

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
                .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND));

        // 기존 하이라이트가 없는 경우
        if (user.getHighlight() == null || user.getHighlight().isEmpty()) {
            throw new SplitException(ErrorCode.HIGHLIGHT_NOT_FOUND);
        }

        user.updateHighlight(highlight);
        userRepository.save(user);

        log.info("Highlight updated for user {}: {}", userId, highlight);
    }
    /** 테마 변경 **/
    public void updateThema(Integer userId, Integer thema) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND));

        user.updateThema(thema);
        log.info("User {} thema updated to {}", userId, thema);
    }

    private boolean isValidVideoUrl(String url) {
        return url != null &&
                url.startsWith("https://split-bucket-first-1.s3.ap-northeast-2.amazonaws.com/") &&
                (url.endsWith(".mov") || url.endsWith(".mp4"));
    }

    public void signupUser(@Valid SignupRequestDto signupRequest) {
        //TODO 응답 및 에러 형식 수정 필요

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
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
}
