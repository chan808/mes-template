package com.sainti.mestemplate.user.application;

import com.sainti.mestemplate.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "User not found"),
    LOGIN_ID_DUPLICATED(HttpStatus.CONFLICT, "USER_002", "Login id already exists"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "USER_003", "Invalid password");

    private final HttpStatus status;
    private final String code;
    private final String message;

    UserErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
