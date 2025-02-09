package com.ssafy.Split.domain.user.controller;

import com.ssafy.Split.domain.user.domain.dto.request.HighlightRequest;
import com.ssafy.Split.domain.user.service.UserService;
import com.ssafy.Split.global.common.exception.ErrorResponse;
import com.ssafy.Split.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;


    @DeleteMapping("/highlight")
    public ResponseEntity<ApiResponse> deleteHighlight(
            @RequestHeader("Authorization") String userId) {  // 임시로 userId로 사용, 나중에 토큰으로 변경 필요

        userService.deleteHighlight(Integer.parseInt(userId));

        return ResponseEntity.ok(ApiResponse.builder()
                .code("SUCCESS")
                .status(200)
                .message("Highlight video deleted successfully")
                .timestamp(LocalDateTime.now().toString())
                .build());
    }
    //TODO 지금은 userId를 직접 받지만, 나중에 JWT 구현시 토큰에서 userId를 추출하도록 수정 필요
    /** 하이라이트 등록 **/
    @PostMapping("/highlight")
    public ResponseEntity<ErrorResponse> createHighlight(
            @RequestHeader("Authorization") String userId,
            @Valid @RequestBody HighlightRequest request) {

        userService.createHighlight(Integer.parseInt(userId), request.getHighlight());

        return ResponseEntity.ok(ErrorResponse.builder()
                .code("SUCCESS")
                .status(200)
                .message("Highlight created successfully")
                .timestamp(LocalDateTime.now().toString())
                .build());
    }


}
