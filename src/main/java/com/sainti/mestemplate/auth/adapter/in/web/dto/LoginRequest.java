package com.sainti.mestemplate.auth.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull Long tenantId,
        @NotBlank String loginId,
        @NotBlank String password
) {
}
