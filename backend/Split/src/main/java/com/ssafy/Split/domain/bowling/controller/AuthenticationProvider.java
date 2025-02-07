package com.ssafy.Split.domain.bowling.controller;

public interface AuthenticationProvider {
    boolean validateToken(String token);
    Long getUserIdFromToken(String token);
}
