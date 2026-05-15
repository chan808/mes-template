package com.sainti.mestemplate.user.application.dto;

import com.sainti.mestemplate.user.domain.UserRole;
import com.sainti.mestemplate.user.domain.UserStatus;

import java.time.LocalDateTime;

public record UserResult(
        Long id,
        Long tenantId,
        String loginId,
        String displayName,
        UserRole role,
        UserStatus status,
        boolean deleted,
        boolean mustChangePassword,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
