package com.ssafy.Split.global.filter;

import com.ssafy.Split.domain.user.domain.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public LoginFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //TODO login방식에 맞게 커스텀
        String email = request.getParameter("email");
        String password = obtainPassword(request);

        log.info("leafdrink : "+email+" "+password);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email,password,null);

        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails data = (CustomUserDetails) authResult.getPrincipal();

        int id = data.getUser().getId();
        String email = data.getUsername();

        //log.info(data.getUsername()+" "+data.getName());
        //토큰 생성
//        log.info(accessTime+" "+refreshTime);
//
//        String access = jwtUtil.createJwt("access",name,id,accessTime);
//        String refresh = jwtUtil.createJwt("refresh",name,id,refreshTime);

        //DB에 Refresh토큰 저장
//        refreshService.deleteByMemberId(id);
//        refreshService.addRefreshEntity(id,refresh,refreshTime);

//        response.setHeader("access",access);

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
