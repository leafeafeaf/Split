package com.ssafy.Split.global.common.exception;

import lombok.Getter;

@Getter
public class SplitException extends RuntimeException {  // 모든 예외 부모
    private final ErrorCode errorCode;
    private final String[] args;


    public SplitException(ErrorCode errorCode, String... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
        this.args = args;
    }
}
