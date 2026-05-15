package com.sainti.mestemplate.auth.adapter.in.web.dto;

import com.sainti.mestemplate.auth.application.dto.LoginResult;

public record LoginResponse(
        String accessToken,
        Long userId,
        String role,
        boolean mustChangePassword
) {
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(
                result.accessToken(),
                result.userId(),
                result.role(),
                result.mustChangePassword()
        );
    }
}
