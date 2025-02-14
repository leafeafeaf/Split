package com.ssafy.Split.global.common.JWT.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.Split.global.common.JWT.config.handler.CustomAuthenticationEntryPoint;
import com.ssafy.Split.global.common.JWT.service.JWTService;
import com.ssafy.Split.global.common.JWT.util.JWTUtil;
import com.ssafy.Split.global.filter.CustomLogoutFilter;
import com.ssafy.Split.global.filter.ExceptionFilter;
import com.ssafy.Split.global.filter.JWTFilter;
import com.ssafy.Split.global.filter.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final JWTService JWTService;
    private final ObjectMapper objectMapper;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Value("${spring.jwt.access.expire-time}")
    private long accessTime;
    @Value("${spring.jwt.refresh.expire-time}")
    private long refreshTime;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //CORS 문제 해결
        http.cors((cors)-> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(Arrays.asList(
                        "*"
//                        "https://i12b202.p.ssafy.io/",
//                        "http://i12b202.p.ssafy.io/"
                ));

                configuration.setAllowedMethods(Collections.singletonList("*"));
                configuration.setAllowCredentials(false);
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setMaxAge(3600L);

                configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                return configuration;
            }
        }));
        http.exceptionHandling(ex ->
                ex.authenticationEntryPoint(customAuthenticationEntryPoint) // ✅ 인증 실패 시 403 JSON 응답 반환
        );
        //csrf disable, 세션을 stateless상태로 관리하기 때문에 csrf 공격에 덜 취약함
        http.csrf((auth) -> auth.disable());
        //Form 로그인 방식 disable
        http.formLogin((auth) -> auth.disable());
        //http basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/user").permitAll()
                .requestMatchers(HttpMethod.POST, "/reissue").permitAll()
                .requestMatchers(HttpMethod.GET, "/rank").permitAll()
                .requestMatchers(HttpMethod.GET, "/device/{serial}/frame/{framenum}").permitAll()
                .requestMatchers(HttpMethod.GET, "/device/{serial}/frame").permitAll()
                .requestMatchers(HttpMethod.POST, "/device/{serial}/frame").permitAll()
                .requestMatchers(HttpMethod.POST, "/device/{serial}/frame/{framenum}/video").permitAll()
                .requestMatchers(HttpMethod.GET, "/user/check-nickname/{nickname}").permitAll()

                // 🔹 인증 필요 (YES)
                .requestMatchers(HttpMethod.POST, "/logout").authenticated()
                .requestMatchers(HttpMethod.GET, "/user").authenticated()
                .requestMatchers(HttpMethod.PUT, "/user").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/user").authenticated()
                .requestMatchers(HttpMethod.PUT, "/thema").authenticated()
                .requestMatchers(HttpMethod.PUT, "/user/highlight").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/user/highlight").authenticated()
                .requestMatchers(HttpMethod.POST, "/device/{serial}").authenticated()
                .requestMatchers(HttpMethod.POST, "/game").authenticated()
                .requestMatchers(HttpMethod.GET, "/game/{Id}").authenticated()
                .requestMatchers(HttpMethod.GET, "/game").authenticated()

                // 기타 모든 요청은 인증 필요
                .anyRequest().authenticated());

        //필터 적용
        http.addFilterBefore(new JWTFilter(jwtUtil),LoginFilter.class);
        http.addFilterBefore(new ExceptionFilter(objectMapper), JWTFilter.class);

        //원래있던 로그인필터 자리에 새롭게 커스텀한 로그인 필터를 넣어라
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), JWTService,jwtUtil,objectMapper,accessTime,refreshTime), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(JWTService,objectMapper), LogoutFilter.class);

        //세션 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
