package com.sainti.mestemplate.user.adapter.in.web.dto;

import com.sainti.mestemplate.user.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank
        @Size(max = 50)
        String loginId,

        @NotBlank
        @Size(min = 8, max = 64)
        String password,

        @NotBlank
        @Size(max = 100)
        String displayName,

        @NotNull
        UserRole role
) {
}
