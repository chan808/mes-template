package com.sainti.mestemplate.user.application.dto;

public record ResetPasswordCommand(
        Long tenantId,
        Long targetUserId,
        Long requestedBy
) {
}
