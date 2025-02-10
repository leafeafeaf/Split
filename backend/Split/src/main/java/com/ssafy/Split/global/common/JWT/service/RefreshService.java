package com.ssafy.Split.global.common.JWT.service;

import com.ssafy.Split.global.common.JWT.util.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshService {
    private final JWTUtil jwtUtil;

    @Value("${spring.jwt.access.expire-time}")
    private long accessTime;
    @Value("${spring.jwt.refresh.expire-time}")
    private long refreshTime;


    public void deleteByMemberId(int id) {

    }

    public void addRefreshEntity(int id, String refresh, long refreshTime) {

    }

    public String reissue(String refreshToken, HttpServletResponse response) throws Exception {

        //TODO 리프레시 토큰 에러 작성
        if (refreshToken == null) throw new Exception("토큰이 비어있음");

        jwtUtil.isExpired(refreshToken);

        String category = jwtUtil.getCategory(refreshToken);
        //TODO 리프레시 토큰 에러 작성
        if (!category.equals("refresh")) throw new Exception("토큰이 리프레시가 아님");

        //TODO 레디스에서 토큰이 남아있는지 확인
        boolean isExist = true;

        //TODO 만료된 리프레시 토큰 에러
        if (!isExist) throw new Exception("이미 만료된거임");

        int id = jwtUtil.getId(refreshToken);
        String email = jwtUtil.getEmail(refreshToken);
        String nickname = jwtUtil.getNickname(refreshToken);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", id, email, nickname, accessTime);
        String newRefresh = jwtUtil.createJwt("refresh", id, email, nickname, refreshTime);

        //TODO 기존 jwt db 삭제
        //TODO 새 jwt db 추가

        response.addCookie(jwtUtil.createCookie("refresh",newRefresh));

        return newAccess;
    }
}
