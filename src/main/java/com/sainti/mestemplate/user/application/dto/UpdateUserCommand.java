package com.sainti.mestemplate.user.application.dto;

import com.sainti.mestemplate.user.domain.UserRole;
import com.sainti.mestemplate.user.domain.UserStatus;

public record UpdateUserCommand(
        Long tenantId,
        Long userId,
        String displayName,
        UserRole role,
        UserStatus status
) {
}
