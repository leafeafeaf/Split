package com.ssafy.Split.global.common.JWT.service;

import com.ssafy.Split.global.common.JWT.util.JWTUtil;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import com.ssafy.Split.global.common.redis.util.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class JWTService {

  private final JWTUtil jwtUtil;
  private final RedisUtil redisUtil;

  @Value("${spring.jwt.access.expire-time}")
  private long accessTime;
  @Value("${spring.jwt.refresh.expire-time}")
  private long refreshTime;

  public String reissue(String refresh, HttpServletResponse response) throws SplitException {

      if (refresh == null) {
          throw new SplitException(ErrorCode.TOKEN_MISSING, "refresh");
      }

    String category = jwtUtil.getCategory(refresh);

      if (!category.equals("refresh")) {
          throw new SplitException(ErrorCode.INVALID_TOKEN);
      }

    boolean isExist = redisUtil.hasKey(refresh);
    if (!isExist) {
      throw new SplitException(ErrorCode.UNREGISTERED_TOKEN);
    }

    int id = jwtUtil.getId(refresh);
    String email = jwtUtil.getEmail(refresh);
    String nickname = jwtUtil.getNickname(refresh);

    //make new JWT
    String newAccess = jwtUtil.createJwt("access", id, email, nickname, accessTime);
    String newRefresh = jwtUtil.createJwt("refresh", id, email, nickname, refreshTime);

    redisUtil.deleteValue(refresh);
    redisUtil.setValue(newRefresh, String.valueOf(id), refreshTime);

    response.addCookie(jwtUtil.createCookie("refresh", newRefresh));

    return newAccess;
  }


  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String refresh = jwtUtil.getCookieValue(request, "refresh")
        .orElseThrow(() -> new SplitException(ErrorCode.TOKEN_MISSING, "refresh"));

    if (jwtUtil.isExpired(refresh)) {
      throw new SplitException(ErrorCode.TOKEN_EXPIRED);
    }

    String category = jwtUtil.getCategory(refresh);
    if (!category.equals("refresh")) {
      //response status code
      throw new SplitException(ErrorCode.INVALID_TOKEN);
    }

    //DB에 저장되어 있는지 확인
    boolean isExist = redisUtil.hasKey(refresh);
    if (!isExist) {
      throw new SplitException(ErrorCode.UNREGISTERED_TOKEN);
    }
    //로그아웃 진행
    //Refresh 토큰 DB에서 제거
    redisUtil.deleteValue(refresh);

    //Refresh 토큰 Cookie 값 0
    Cookie cookie = new Cookie("refresh", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");

    response.addCookie(cookie);
    response.setStatus(HttpServletResponse.SC_OK);
  }


  public void deleteRefreshToken(String refresh) {
    redisUtil.deleteValue(refresh);
  }

  public void addRefreshEntity(int id, String refresh) {
    redisUtil.setValue(refresh, String.valueOf(id), refreshTime);
  }
}
