package com.ssafy.Split.domain.bowling.exception;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String message) {
        super(message);
    }

    public DeviceNotFoundException() {
        super("디바이스를 찾을 수 없습니다.");
    }
}
