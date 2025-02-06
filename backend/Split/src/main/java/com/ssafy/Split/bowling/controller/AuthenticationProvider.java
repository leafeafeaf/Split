package com.ssafy.Split.bowling.controller;

public interface AuthenticationProvider {
    boolean validateToken(String token);
    Long getUserIdFromToken(String token);
}
