package com.sainti.mestemplate.user.adapter.in.web.dto;

import com.sainti.mestemplate.user.application.dto.UserResult;
import com.sainti.mestemplate.user.domain.UserRole;
import com.sainti.mestemplate.user.domain.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String loginId,
        String displayName,
        UserRole role,
        UserStatus status,
        boolean mustChangePassword,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static UserResponse from(UserResult result) {
        return new UserResponse(
                result.id(),
                result.loginId(),
                result.displayName(),
                result.role(),
                result.status(),
                result.mustChangePassword(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
