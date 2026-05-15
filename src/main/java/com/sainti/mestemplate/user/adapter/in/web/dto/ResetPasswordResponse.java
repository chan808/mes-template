package com.sainti.mestemplate.user.adapter.in.web.dto;

import com.sainti.mestemplate.user.application.dto.ResetPasswordResult;

public record ResetPasswordResponse(String temporaryPassword) {

    public static ResetPasswordResponse from(ResetPasswordResult result) {
        return new ResetPasswordResponse(result.temporaryPassword());
    }
}
