package com.sainti.mestemplate.user.adapter.in.web.dto;

import com.sainti.mestemplate.user.domain.UserRole;
import com.sainti.mestemplate.user.domain.UserStatus;

public record UserSearch(
        String loginId,
        String displayName,
        UserRole role,
        UserStatus status
) {
}
