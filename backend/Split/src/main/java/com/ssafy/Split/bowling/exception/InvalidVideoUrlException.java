package com.ssafy.Split.bowling.exception;

public class InvalidVideoUrlException extends RuntimeException {
    public InvalidVideoUrlException(String videoUrlAlreadyExists) {
        super(videoUrlAlreadyExists);
    }
}
