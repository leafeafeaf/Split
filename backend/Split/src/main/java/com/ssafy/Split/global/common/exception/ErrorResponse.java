package com.ssafy.Split.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private int status;
    private String message;
    private String timestamp;
    private Object data;

    public static ErrorResponse of(ErrorCode errorCode, String... args) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus())
                .message(String.format(errorCode.getMessage(), args))
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    public static ErrorResponse success(String message, Object data) {
        return ErrorResponse.builder()
                .code("SUCCESS")
                .status(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

}
