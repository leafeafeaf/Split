package com.ssafy.Split.global.common.JWT.controller;

import com.ssafy.Split.global.common.JWT.service.RefreshService;
import com.ssafy.Split.global.common.response.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class RefreshController {
    private final RefreshService refreshService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request,HttpServletResponse response) {
        try {
            //TODO 에러 메시지 정형화 필요
            String refreshToken = getCookieValue(request, "refresh")
                    .orElseThrow(() -> new RuntimeException("Refresh Token not found in cookies"));

            String token = refreshService.reissue(refreshToken,response);

            return ResponseEntity.ok().header("Authorization", token).
                    body(ApiResponse.builder()
                            .code("SUCCESS")
                            .status(200)
                            .message("reissue access Token successfully")
                            .timestamp(LocalDateTime.now().toString())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    private Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findFirst();
    }
}
