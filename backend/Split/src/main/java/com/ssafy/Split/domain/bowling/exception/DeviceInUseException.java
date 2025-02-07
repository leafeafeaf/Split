package com.ssafy.Split.domain.bowling.exception;

public class DeviceInUseException extends RuntimeException {
    public DeviceInUseException(String message) {
        super(message);
    }

    public DeviceInUseException() {
        super("디바이스가 이미 사용 중입니다.");
    }
}
