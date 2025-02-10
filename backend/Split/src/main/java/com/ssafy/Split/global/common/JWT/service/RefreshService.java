package com.ssafy.Split.global.common.JWT.service;

import com.ssafy.Split.global.common.JWT.util.JWTUtil;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import com.ssafy.Split.global.common.redis.util.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshService {
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Value("${spring.jwt.access.expire-time}")
    private long accessTime;
    @Value("${spring.jwt.refresh.expire-time}")
    private long refreshTime;

    public String reissue(String refresh, HttpServletResponse response) throws SplitException {

        //TODO 리프레시 토큰 에러 작성
        if (refresh == null) throw new SplitException(ErrorCode.TOKEN_MISSING,"refresh");

        String category = jwtUtil.getCategory(refresh);
        //TODO 리프레시 토큰 에러 작성
        if (!category.equals("refresh")) throw new SplitException(ErrorCode.INVALID_TOKEN);

        boolean isExist = redisUtil.hasKey(refresh);
        //TODO 만료된 리프레시 토큰 에러
        if (!isExist) throw new SplitException(ErrorCode.TOKEN_EXPIRED);

        int id = jwtUtil.getId(refresh);
        String email = jwtUtil.getEmail(refresh);
        String nickname = jwtUtil.getNickname(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", id, email, nickname, accessTime);
        String newRefresh = jwtUtil.createJwt("refresh", id, email, nickname, refreshTime);

        redisUtil.deleteValue(refresh);
        redisUtil.setValue(refresh, String.valueOf(id),refreshTime);

        response.addCookie(jwtUtil.createCookie("refresh",newRefresh));

        return newAccess;
    }

    public void deleteRefreshToken(String refresh) {
        redisUtil.deleteValue(refresh);
    }

    public void addRefreshEntity(int id, String refresh) {
        redisUtil.setValue(refresh, String.valueOf(id),refreshTime);
    }
}
