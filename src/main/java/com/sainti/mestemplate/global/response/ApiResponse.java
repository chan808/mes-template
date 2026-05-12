package com.sainti.mestemplate.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean success,
    String message,
    String errorCode,
    T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "OK", null, data);
    }

    public static ApiResponse<Void> successVoid() {
        return new ApiResponse<>(true, "OK", null, null);
    }

    public static ApiResponse<Void> fail(String errorCode, String message) {
        return new ApiResponse<>(false, message, errorCode, null);
    }
}
