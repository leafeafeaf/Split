package com.ssafy.Split.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common Error Codes
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(500, "C002", "서버 오류가 발생했습니다."),
    UNAUTHORIZED(401, "C003", "인증되지 않은 접근입니다.");

    private final int status;
    private final String code;
    private final String message;
}
