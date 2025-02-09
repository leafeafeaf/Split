package com.ssafy.Split.domain.user.controller;

import com.ssafy.Split.domain.user.domain.dto.request.HighlightRequest;
import com.ssafy.Split.domain.user.domain.dto.request.SignupRequestDto;
import com.ssafy.Split.domain.user.domain.dto.request.ThemaRequest;
import com.ssafy.Split.domain.user.service.UserService;
import com.ssafy.Split.global.common.exception.ErrorResponse;
import com.ssafy.Split.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/test")
    public String test(){
        return "로그인 성공해서 여기 잘들어옴";
    }

    @PostMapping
    public ResponseEntity<?> signup(@ModelAttribute @Valid SignupRequestDto signupRequest, BindingResult bindingResult){
        //TODO 반환 메시지 정리 필요

        // 1️⃣ 검증 실패 시 에러 메시지 반환
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        userService.signupUser(signupRequest);
        return ResponseEntity.ok("회원가입 성공!");
    }

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
    @PatchMapping("/highlight")  // PUT에서 PATCH로 변경
    public ResponseEntity<ErrorResponse> updateHighlight(
            @RequestHeader("Authorization") String userId,
            @Valid @RequestBody HighlightRequest request) {

        userService.updateHighlight(Integer.parseInt(userId), request.getHighlight());

        return ResponseEntity.ok(ErrorResponse.builder()
                .code("SUCCESS")
                .status(200)
                .message("Highlight updated successfully")
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @PatchMapping("/thema")  // PATCH 메서드 사용
    public ResponseEntity<ApiResponse> updateThema(
            @RequestHeader("Authorization") String userId,  // 임시로 userId로 사용
            @Valid @RequestBody ThemaRequest request) {

        userService.updateThema(Integer.parseInt(userId), request.getValidThema());

        return ResponseEntity.ok(ApiResponse.builder()
                .code("SUCCESS")
                .status(200)
                .message("put thema successfully")
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

}
