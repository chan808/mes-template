package com.sainti.mestemplate.user.application.dto;

public record ChangeMyPasswordCommand(
        Long tenantId,
        Long userId,
        String currentPassword,
        String newPassword
) {
}
