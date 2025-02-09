package com.ssafy.Split.global.filter;

import com.ssafy.Split.domain.user.domain.dto.CustomUserDetails;
import com.ssafy.Split.domain.user.domain.entity.User;
import com.ssafy.Split.global.common.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("Authorization");
        //토큰이 없으면 다음 필터로 넘김 (토큰이 필요없는 요청도 있을 수 있기 때문에)
        if(accessToken == null){
            filterChain.doFilter(request,response);
            return;
        }

        //토큰 만료 여부 확인, 만료시 다음 필터로 안넘김
        try{
            jwtUtil.isExpired(accessToken);
        }
        catch (ExpiredJwtException e){
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }catch (SignatureException e){
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invaild signature");
            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        //토큰이 access인지 확인(발급 시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if(!category.equals("access")){
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");
            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //일시적인 세션 생성
        int id = jwtUtil.getId(accessToken);
        String email = jwtUtil.getEmail(accessToken);
        String nickname = jwtUtil.getNickname(accessToken);

        User user = User.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request,response);
    }
}