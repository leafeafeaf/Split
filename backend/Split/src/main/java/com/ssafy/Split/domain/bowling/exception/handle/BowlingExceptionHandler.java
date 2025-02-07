package com.ssafy.Split.domain.bowling.exception.handle;

import com.ssafy.Split.domain.bowling.exception.DeviceInUseException;
import com.ssafy.Split.domain.bowling.exception.DeviceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice("com.ssafy.Split.bowling")
public class BowlingExceptionHandler {

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<String> handleDeviceNotFoundException(DeviceNotFoundException e) {
        log.error("Device not found", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(DeviceInUseException.class)
    public ResponseEntity<String> handleDeviceInUseException(DeviceInUseException e) {
        log.error("Device in use", e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
