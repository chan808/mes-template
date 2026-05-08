package com.sainti.mestemplate.user.adapter.in.web.dto;

import com.sainti.mestemplate.user.domain.UserRole;
import com.sainti.mestemplate.user.domain.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank
        @Size(max = 100)
        String displayName,

        @NotNull
        UserRole role,

        @NotNull
        UserStatus status
) {
}
