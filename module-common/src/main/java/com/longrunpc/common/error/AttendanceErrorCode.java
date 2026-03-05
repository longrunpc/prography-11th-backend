package com.longrunpc.common.error;

public enum AttendanceErrorCode implements ErrorCode {
    
    ATTENDANCE_NOT_FOUND(404, "ATTENDANCE_001", "출결 기록을 찾을 수 없습니다."),
    ATTENDANCE_ALREADY_CHECKED(409, "ATTENDANCE_002", "이미 출결 체크가 완료되었습니다.");

    private final int status;
    private final String code;
    private final String message;

    AttendanceErrorCode(int status, String code, String message) {
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
