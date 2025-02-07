package com.ssafy.Split.domain.bowling.exception;

public class InvalidVideoUrlException extends RuntimeException {
    public InvalidVideoUrlException(String videoUrlAlreadyExists) {
        super(videoUrlAlreadyExists);
    }
}
