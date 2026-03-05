package com.longrunpc.common.error;

public enum GlobalErrorCode implements ErrorCode {
    INVALID_INPUT(400, "COMMON_001", "입력값이 올바르지 않습니다."),
    INTERNAL_ERROR(500, "COMMON_002", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;

    GlobalErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
    
    @Override
    public int getStatus() {
        return status;
    }
}
