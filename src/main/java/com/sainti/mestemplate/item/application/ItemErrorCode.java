package com.sainti.mestemplate.item.application;

import com.sainti.mestemplate.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ItemErrorCode implements ErrorCode {

    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEM_001", "Item not found"),
    ITEM_CODE_DUPLICATED(HttpStatus.CONFLICT, "ITEM_002", "Item code already exists");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ItemErrorCode(HttpStatus status, String code, String message) {
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
