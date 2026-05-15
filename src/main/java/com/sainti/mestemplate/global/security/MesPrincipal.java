package com.sainti.mestemplate.global.security;

import com.sainti.mestemplate.user.domain.UserRole;

public record MesPrincipal(
        Long userId,
        Long tenantId,
        UserRole role,
        boolean mustChangePassword
) {
}
