package com.ssafy.Split.global.common.JWT.controller;

import com.ssafy.Split.global.common.JWT.service.JWTService;
import com.ssafy.Split.global.common.JWT.util.JWTUtil;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import com.ssafy.Split.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@AllArgsConstructor
public class RefreshController {
    private final JWTService JWTService;
    private final JWTUtil jwtUtil;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request,HttpServletResponse response) throws Exception {

        String refreshToken = jwtUtil.getCookieValue(request, "refresh")
                    .orElseThrow(() -> new SplitException(ErrorCode.TOKEN_MISSING,"refresh"));

            String token = JWTService.reissue(refreshToken,response);
            log.info("토큰 재발급");
            return ResponseEntity.ok().header("Authorization", token).
                    body(ApiResponse.builder()
                            .code("SUCCESS")
                            .status(200)
                            .message("reissue access Token successfully")
                            .timestamp(LocalDateTime.now().toString())
                            .build());

    }


}
