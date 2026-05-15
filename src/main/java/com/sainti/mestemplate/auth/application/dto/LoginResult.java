package com.sainti.mestemplate.auth.application.dto;

public record LoginResult(
        String accessToken,
        Long userId,
        String role,
        boolean mustChangePassword
) {
}
