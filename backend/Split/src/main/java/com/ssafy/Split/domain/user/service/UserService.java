package com.ssafy.Split.domain.user.service;

import com.ssafy.Split.domain.user.domain.entity.User;
import com.ssafy.Split.domain.user.repository.UserRepository;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import com.ssafy.Split.global.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;


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

    private boolean isValidVideoUrl(String url) {
        return url != null &&
                url.startsWith("s3://split-bucket-first-1/") &&
                (url.endsWith(".mov") || url.endsWith(".mp4"));
    }
}
