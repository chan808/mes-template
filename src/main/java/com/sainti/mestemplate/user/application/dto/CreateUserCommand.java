package com.sainti.mestemplate.user.application.dto;

import com.sainti.mestemplate.user.domain.UserRole;

public record CreateUserCommand(
        Long tenantId,
        String loginId,
        String rawPassword,
        String displayName,
        UserRole role
) {
}
