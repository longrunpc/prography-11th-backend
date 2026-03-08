package com.longrunpc.common.response;

import com.longrunpc.common.error.ErrorCode;

public record ApiResponse<T>(
    boolean success,
    T data,
    ErrorResponse error
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, ErrorResponse.of(errorCode));
    }
}
