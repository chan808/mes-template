package com.sainti.mestemplate.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sainti.mestemplate.global.error.CommonErrorCode;
import com.sainti.mestemplate.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MesAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ApiResponse.fail(CommonErrorCode.UNAUTHORIZED.getCode(), CommonErrorCode.UNAUTHORIZED.getMessage())
                )
        );
    }
}
