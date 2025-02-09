package com.ssafy.Split.global.filter;

import com.ssafy.Split.domain.user.domain.dto.CustomUserDetails;
import com.ssafy.Split.global.common.util.JWTUtil;
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
    private final JWTUtil jwtUtil;
    private long accessTime;
    private long refreshTime;

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = request.getParameter("email");
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email,password,null);
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

        //log.info(data.getUsername()+" "+data.getName());
        //토큰 생성
        log.info(accessTime+" "+refreshTime);
//
        String access = jwtUtil.createJwt("access",id,email,nickname,accessTime);
        String refresh = jwtUtil.createJwt("refresh",id,email,nickname,refreshTime);

        //DB에 Refresh토큰 저장
//        refreshService.deleteByMemberId(id);
//        refreshService.addRefreshEntity(id,refresh,refreshTime);

        response.setHeader("Authorization",access);

        //refresh 토큰 HTTPONLY 쿠키로 브라우저로 전송
//        response.addCookie(jwtUtil.createCookie("refresh",refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    //로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
//        log.info("fail");
        //TODO 전역 에러 처리를 할 수 있도록 앞단에서 처리해줘야함
        response.setStatus(401);
    }
}
