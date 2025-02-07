package com.ssafy.Split.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /**
     * 공통적 에러코드 처리 에러 코드의 카탈로그 각 에러는 상태코드, 에러코드 메시지를 가짐
     *  모든 에러코드들을 한곳에 체계적으로 정리할 수 있음
     */

    // Common Error Codes
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(500, "C002", "서버 오류가 발생했습니다."),
    UNAUTHORIZED(401, "C003", "인증되지 않은 접근입니다."),

    // Frame
    FRAME_NOT_FOUND(404, "F001", "FRAME not found with Num: %s"),

    // Progress
    PROGRESS_NOT_FOUND(404, "P001", "Progress not found with id: %s"),

    //game
    GAME_NOT_FOUND(404, "G001", "Game not found with id: %s"),
    GAME_ALREADY_DELETED(404, "G002", "Game results have already been viewed and deleted"),

    //user
    USER_NOT_FOUND(404, "U001", "User not found with id: %s");

    private final int status; // HTTP 상태코드
    private final String code; // 비즈니스 에러
    private final String message; // 에러 메시지 템플릿
}
