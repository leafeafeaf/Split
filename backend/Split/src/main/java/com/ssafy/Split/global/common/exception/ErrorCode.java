package com.ssafy.Split.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  /**
   * 공통적 에러코드 처리 에러 코드의 카탈로그 각 에러는 상태코드, 에러코드 메시지를 가짐 모든 에러코드들을 한곳에 체계적으로 정리할 수 있음
   */

  // Common Error Codes
  INVALID_INPUT_VALUE(400, "C001", "잘못된 입력값입니다."),
  INTERNAL_SERVER_ERROR(500, "C002", "서버 오류가 발생했습니다."),
  UNAUTHORIZED(401, "C003", "인증되지 않은 접근입니다."),

  // Token
  TOKEN_EXPIRED(401, "T001", "token has expired"),
  INVALID_TOKEN(401, "T002", "Invalid token format or signature"),
  TOKEN_MISSING(401, "T003", "%s token is missing"),
  TOKEN_ERROR(401, "T004", "Token validation error"),
  UNREGISTERED_TOKEN(401, "T005", "Token is not registered in the system"),

  //Permission
  FORBIDDEN_ACCESS(403, "P001", "Forbidden: You do not have permission to access this resource"),

  // Frame
  FRAME_NOT_FOUND(404, "F001", "FRAME not found with Num: %s"),
  // Device
  DEVICE_NOT_FOUND(404, "D001", "Device not found with serial: %s"),
  DEVICE_IN_USE(409, "D002", "Device is already in use: %s"),


  // Progress
  PROGRESS_NOT_FOUND(404, "P001", "Progress not found with id: %s"),

  //game
  GAME_NOT_FOUND(404, "G001", "Game not found with id: %s"),
  GAME_ALREADY_DELETED(404, "G002", "Game results have already been viewed and deleted"),

  //user
  USER_NOT_FOUND(404, "U001", "User not found with id: %s"),
  INVALID_CREDENTIALS(401, "U002", "Invalid email or password"),
  USER_MISMATCH(403, "U003", "Registered user information does not match the current login"),

  //highlight
  INVALID_VIDEO_URL(400, "H001", "Invalid video URL format"),
  HIGHLIGHT_ALREADY_EXISTS(400, "H002", "Highlight already exists for this user"),
  HIGHLIGHT_NOT_FOUND(404, "H003", "Highlight not found for this user"),

  //s3
  S3_DELETE_ERROR(500, "S001", "Error deleting file from S3"),
  INVALID_FILE_URL(400, "S002", "Invalid file URL format"),

  //video
  VIDEO_ALREADY_EXISTS(400, "V001", "Video URL already exists");

  private final int status; // HTTP 상태코드
  private final String code; // 비즈니스 에러
  private final String message; // 에러 메시지 템플릿
}
