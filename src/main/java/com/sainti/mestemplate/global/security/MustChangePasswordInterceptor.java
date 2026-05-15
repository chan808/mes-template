package com.sainti.mestemplate.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class MustChangePasswordInterceptor implements HandlerInterceptor {

    private static final String CHANGE_PASSWORD_PATH = "/api/v1/users/me/password";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof MesPrincipal principal)) {
            return true;
        }

        if (!principal.mustChangePassword()) {
            return true;
        }

        // mustChangePassword = true 이면 비밀번호 변경 API 외 모든 요청 차단
        if (CHANGE_PASSWORD_PATH.equals(request.getRequestURI())
                && "PATCH".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"success\":false,\"message\":\"Password change required before proceeding\",\"errorCode\":\"MUST_CHANGE_PASSWORD\"}"
        );
        return false;
    }
}
