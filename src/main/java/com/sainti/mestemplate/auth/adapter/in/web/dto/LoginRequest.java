package com.sainti.mestemplate.auth.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LoginRequest(
        @NotNull @Positive Long tenantId,
        @NotBlank String loginId,
        @NotBlank String password
) {
}
