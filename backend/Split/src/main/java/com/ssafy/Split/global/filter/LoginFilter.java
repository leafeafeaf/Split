package com.ssafy.Split.global.filter;

import com.ssafy.Split.global.common.JWT.domain.CustomUserDetails;
import com.ssafy.Split.global.common.JWT.service.RefreshService;
import com.ssafy.Split.global.common.JWT.util.JWTUtil;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final RefreshService refreshService;
    private final JWTUtil jwtUtil;
    private long accessTime;
    private long refreshTime;

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = request.getParameter("email");
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);
        //자동으로 CustomUserDetailsService 호출
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails data = (CustomUserDetails) authResult.getPrincipal();

        //TODO refresh토큰 개발 필요

        //로그인 정보를 바탕으로
        int id = data.getUser().getId();
        String email = data.getUser().getEmail();
        String nickname = data.getUser().getNickname();

        //새 토큰 생성
        String access = jwtUtil.createJwt("access", id, email, nickname, accessTime);
        String refresh = jwtUtil.createJwt("refresh", id, email, nickname, refreshTime);

        //기존 리프레시 토큰 삭제
        String pastRefresh = jwtUtil.getCookieValue(request, "refresh")
                .orElse(null);

        if (pastRefresh != null && !pastRefresh.isEmpty()) {
            refreshService.deleteRefreshToken(pastRefresh);
        }
        refreshService.addRefreshEntity(id, refresh);

        response.setHeader("Authorization", access);
        response.addCookie(jwtUtil.createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    //로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        throw new SplitException(ErrorCode.INVALID_CREDENTIALS);
    }
}
