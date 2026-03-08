package com.longrunpc.common.response;

import com.longrunpc.common.error.ErrorCode;

public record ErrorResponse(
    String code,
    String message
) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }
}
