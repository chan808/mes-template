package com.sainti.mestemplate.user.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeMyPasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8, max = 64) String newPassword
) {
}
