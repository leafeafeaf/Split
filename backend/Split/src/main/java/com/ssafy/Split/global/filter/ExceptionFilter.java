package com.ssafy.Split.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.Split.global.common.exception.ErrorResponse;
import com.ssafy.Split.global.common.exception.SplitException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (SplitException e) {
            setErrorResponse(response, e);
        } catch (Exception e) {
            setErrorResponse(response, e);
        }
    }

    private void setErrorResponse(HttpServletResponse response, Exception e) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse;
        int status;

        if (e instanceof SplitException splitException) {
            status = splitException.getErrorCode().getStatus();
            errorResponse = ErrorResponse.of(splitException.getErrorCode(), splitException.getArgs());
        } else {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            errorResponse = new ErrorResponse(
                    "E999",
                    status,
                    "Internal Server Error",
                    LocalDateTime.now().toString(),
                    null
            );
        }

        response.setStatus(status);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
