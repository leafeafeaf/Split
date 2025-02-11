package com.ssafy.Split.domain.user.controller;

import com.ssafy.Split.global.common.JWT.domain.CustomUserDetails;
import com.ssafy.Split.domain.user.domain.dto.request.HighlightRequest;
import com.ssafy.Split.domain.user.domain.dto.request.SignupRequestDto;
import com.ssafy.Split.domain.user.domain.dto.request.ThemaRequest;
import com.ssafy.Split.domain.user.service.UserService;
import com.ssafy.Split.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

  private final UserService userService;

  @GetMapping("/test")
  public String test() {
    return "로그인 성공해서 여기 잘들어옴";
  }

  @PostMapping
  public ResponseEntity<?> signup(@ModelAttribute @Valid SignupRequestDto signupRequest,
      BindingResult bindingResult) {
    //TODO 반환 메시지 정리 필요

    // 1️⃣ 검증 실패 시 에러 메시지 반환
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
    }
    userService.signupUser(signupRequest);
    return ResponseEntity.ok("회원가입 성공!");
  }

  /**
   * 하이라이트 삭제
   **/
  @DeleteMapping("/highlight")
  public ResponseEntity<ApiResponse> deleteHighlight() {

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    int userId = userDetails.getUser().getId();
    userService.deleteHighlight(userId);

    return ResponseEntity.ok(ApiResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("Highlight video deleted successfully")
        .timestamp(LocalDateTime.now().toString())
        .build());
  }

  /**
   * 하이라이트 등록
   **/
  @PostMapping("/highlight")
  public ResponseEntity<ApiResponse> createHighlight(
      @Valid @RequestBody HighlightRequest request) {

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    int userId = userDetails.getUser().getId();
    userService.createHighlight(userId, request.getHighlight());

    return ResponseEntity.ok(ApiResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("Highlight created successfully")
        .timestamp(LocalDateTime.now().toString())
        .build());
  }

  /**
   * 하이라이트 수정
   **/
  @PatchMapping("/highlight")
  public ResponseEntity<ApiResponse> updateHighlight(
      @Valid @RequestBody HighlightRequest request) {

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    int userId = userDetails.getUser().getId();
    userService.updateHighlight(userId, request.getHighlight());

    return ResponseEntity.ok(ApiResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("Highlight updated successfully")
        .timestamp(LocalDateTime.now().toString())
        .build());
  }

  /**
   * 테마 수정
   **/
  @PatchMapping("/thema")
  public ResponseEntity<ApiResponse> updateThema(
      @Valid @RequestBody ThemaRequest request) {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    int userId = userDetails.getUser().getId();
    log.info("userId : {}", userId);

    userService.updateThema(userId, request.getValidThema());

    return ResponseEntity.ok(ApiResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("put thema successfully")
        .timestamp(LocalDateTime.now().toString())
        .build());
  }
}
