package com.longrunpc.common.error;

public enum SessionErrorCode implements ErrorCode {

    SESSION_NOT_FOUND(404, "SESSION_001", "일정을 찾을 수 없습니다."),
    SESSION_ALREADY_CANCELLED(400, "SESSION_002", "이미 취소된 일정입니다."),
    SESSION_NOT_IN_PROGRESS(400, "SESSION_003", "진행 중인 일정이 아닙니다."),
    QR_NOT_FOUND(404, "SESSION_004", "QR 코드를 찾을 수 없습니다."),
    QR_INVALID(400, "SESSION_005", "유효하지 않은 QR 코드입니다."),
    QR_EXPIRED(400, "SESSION_006", "만료된 QR 코드입니다."),
    QR_ALREADY_ACTIVE(409, "SESSION_007", "이미 활성화된 QR 코드가 있습니다.");

    private final int status;
    private final String code;
    private final String message;

    SessionErrorCode(int status, String code, String message) {
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
