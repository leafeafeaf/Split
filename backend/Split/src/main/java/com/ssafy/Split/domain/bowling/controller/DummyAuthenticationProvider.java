package com.ssafy.Split.domain.bowling.controller;


import org.springframework.stereotype.Component;

// 임시 구현체 ( 토큰 검증 없이 항상 true 반환 )
@Component
public class DummyAuthenticationProvider implements AuthenticationProvider {

    @Override
    public boolean validateToken(String token) {
        // 현재는 무조건 true 반환
        return true;
    }

    @Override
    public Long getUserIdFromToken(String token) {
        // 현재는 요청으로 들어온 userID를 그대로 사용

        return null; // null을 반환하면 Service에서 요청값 사용
    }
}
