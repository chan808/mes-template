package com.sainti.mestemplate.user.application.dto;

import com.sainti.mestemplate.user.domain.UserRole;
import com.sainti.mestemplate.user.domain.UserStatus;

public record UserQuery(
        String loginId,
        String displayName,
        UserRole role,
        UserStatus status
) {
}
