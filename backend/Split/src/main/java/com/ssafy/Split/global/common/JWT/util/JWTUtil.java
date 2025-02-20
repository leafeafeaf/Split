package com.ssafy.Split.global.common.JWT.util;

import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JWTUtil {

  private final SecretKey secretKey;

  @Value("${spring.jwt.access.expire-time}")
  private long accessTime;
  @Value("${spring.jwt.refresh.expire-time}")
  private long refreshTime;

  public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  public String getEmail(String token) {
    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .get("email", String.class);
    } catch (JwtException e) {
      throw handleJwtException(e);
    }
  }

  public String getNickname(String token) {
    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .get("nickname", String.class);
    } catch (JwtException e) {
      throw handleJwtException(e);
    }
  }

  public int getId(String token) {
    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .get("id", Integer.class);
    } catch (JwtException e) {
      throw handleJwtException(e);
    }
  }


  public String getCategory(String token) {
    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .get("category", String.class);
    } catch (JwtException e) {
      throw handleJwtException(e);
    }
  }

  public boolean isExpired(String token) {
    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .getExpiration()
          .before(new Date());
    } catch (JwtException e) {
      throw handleJwtException(e);
    }
  }

  public Cookie createCookie(String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge((int) refreshTime);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    return cookie;
  }

  public String createJwt(String category, int id, String email, String nickname, Long expiredMs) {
    long currentTime = System.currentTimeMillis();
    long expirationTime = currentTime + expiredMs;

    return Jwts.builder()
        .claim("category", category)
        .claim("id", id)
        .claim("email", email)
        .claim("nickname", nickname)
        .issuedAt(new Date(currentTime))
        .expiration(new Date(expirationTime))
        .signWith(secretKey)
        .compact();
  }


  public Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
    if (request.getCookies() == null) {
      return Optional.empty();
    }

    return Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(cookieName))
        .map(Cookie::getValue)
        .findFirst();
  }

  private SplitException handleJwtException(JwtException e) {
    log.error(e.getMessage());

    if (e instanceof ExpiredJwtException) {
      return new SplitException(ErrorCode.TOKEN_EXPIRED);
    } else if (e instanceof MalformedJwtException) {
      return new SplitException(ErrorCode.INVALID_TOKEN);
    } else if (e instanceof UnsupportedJwtException) {
      return new SplitException(ErrorCode.INVALID_TOKEN);
    } else {
      return new SplitException(ErrorCode.TOKEN_ERROR);
    }
  }
}