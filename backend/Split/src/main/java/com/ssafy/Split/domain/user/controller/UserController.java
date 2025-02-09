package com.ssafy.Split.domain.user.controller;

import com.ssafy.Split.domain.user.service.UserService;
import com.ssafy.Split.global.common.response.ApiResponse;
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


}
