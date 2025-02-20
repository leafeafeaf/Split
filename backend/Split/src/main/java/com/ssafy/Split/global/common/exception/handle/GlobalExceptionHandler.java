package com.ssafy.Split.global.common.exception.handle;


import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.ErrorResponse;
import com.ssafy.Split.global.common.exception.SplitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SplitException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(SplitException e) {
        log.error("SplitException: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getArgs());
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
