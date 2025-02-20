package com.ssafy.Split.domain.user.controller;

import com.ssafy.Split.domain.user.domain.dto.request.HighlightRequest;
import com.ssafy.Split.domain.user.domain.dto.request.SignupRequestDto;
import com.ssafy.Split.domain.user.domain.dto.request.ThemaRequest;
import com.ssafy.Split.domain.user.domain.dto.request.UpdateUserRequestDto;
import com.ssafy.Split.domain.user.domain.dto.response.UserInfoResponseDto;
import com.ssafy.Split.domain.user.service.UserService;
import com.ssafy.Split.global.common.JWT.domain.CustomUserDetails;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import com.ssafy.Split.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<ApiResponse> signup(@ModelAttribute @Valid SignupRequestDto signupRequest,
      BindingResult bindingResult) {

    // 1️⃣ 검증 실패 시 에러 메시지 반환
    if (bindingResult.hasErrors()) {
      //에러 모두 모으기
      StringBuilder errorField = new StringBuilder();

      bindingResult.getAllErrors().stream()
          .filter(error -> error instanceof FieldError) // FieldError만 필터링
          .map(error -> ((FieldError) error).getField()) // Field 값 추출
          .forEach(field -> errorField.append(field).append(", ")); // StringBuilder에 추가

      if (!errorField.isEmpty()) {
        errorField.setLength(errorField.length() - 2);
      }

      throw new SplitException(ErrorCode.INVALID_INPUT_VALUE, errorField.toString());
    }

    userService.signupUser(signupRequest);
    log.info("회원가입");

    return ResponseEntity.status(HttpStatus.CREATED) // 201 Created
        .body(ApiResponse.builder()
            .code("SUCCESS")
            .status(HttpStatus.CREATED.value()) // 201
            .message("Successfully registered")
            .timestamp(LocalDateTime.now().toString())
            .build()
        );
  }

  @GetMapping
  public ResponseEntity<UserInfoResponseDto> getUserInfo() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();

    UserInfoResponseDto.UserData data = userService.getUserInfo(userDetails.getUser().getId());
    log.info("유저조회 - userId : {}", userDetails.getUser().getId());

    return ResponseEntity.ok(UserInfoResponseDto.builder()
        .code("SUCCESS")
        .status(200)
        .message("Successfully get user info")
        .timestamp(LocalDateTime.now().toString())
        .data(data)
        .build());
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse> deleteUser() {
    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();

    userService.deleteUser(userDetails.getUser().getId());
    log.info("유저탈퇴 - userId : {}", userDetails.getUser().getId());

    return ResponseEntity.ok(ApiResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("Successfully deleted")
        .timestamp(LocalDateTime.now().toString())
        .build());
  }

  @PutMapping
  public ResponseEntity<ApiResponse> updateUser(
      @RequestBody @Valid UpdateUserRequestDto updateRequest
      , BindingResult bindingResult
  ) {
    if (bindingResult.hasErrors()) {
      //에러 모두 모으기
      StringBuilder errorField = new StringBuilder();

      bindingResult.getAllErrors().stream()
          .filter(error -> error instanceof FieldError) // FieldError만 필터링
          .map(error -> ((FieldError) error).getField()) // Field 값 추출
          .forEach(field -> errorField.append(field).append(", ")); // StringBuilder에 추가

      if (!errorField.isEmpty()) {
        errorField.setLength(errorField.length() - 2);
      }

      throw new SplitException(ErrorCode.INVALID_INPUT_VALUE, errorField.toString());
    }

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();

    userService.updateUser(userDetails.getUser(), updateRequest);
    log.info("유저 정보 수정 - userId: {}", userDetails.getUser().getId());

    return ResponseEntity.ok(ApiResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("Successfully updated")
        .timestamp(LocalDateTime.now().toString())
        .build());
  }

  @GetMapping("/check-nickname/{nickname}")
  public ResponseEntity<ApiResponse> checkNickname(@PathVariable String nickname) {

    userService.checkNickname(nickname);
    log.info("닉네임 중복확인 - 닉네임 : {}", nickname);

    return ResponseEntity.ok(ApiResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("Available")
        .timestamp(LocalDateTime.now().toString())
        .build());
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
    log.info("하이라이트 삭제 : userId : {}", userId);

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
    log.info("하이라이트 등록 : userId : {}", userId);

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
    log.info("하이라이트 수정 : userId : {}", userId);

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

    userService.updateThema(userId, request.getValidThema());
    log.info("테마 수정 : userId : {}", userId);

    return ResponseEntity.ok(ApiResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("put thema successfully")
        .timestamp(LocalDateTime.now().toString())
        .build());
  }
}
