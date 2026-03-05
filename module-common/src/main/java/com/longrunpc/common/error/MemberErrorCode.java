package com.longrunpc.common.error;

public enum MemberErrorCode implements ErrorCode {

    LOGIN_FAILED(401, "MEMBER_001", "로그인 아이디 또는 비밀번호가 올바르지 않습니다."),
    MEMBER_WITHDRAWN(403, "MEMBER_002", "탈퇴한 회원입니다."),
    MEMBER_NOT_FOUND(404, "MEMBER_003", "회원을 찾을 수 없습니다."),
    DUPLICATE_LOGIN_ID(409, "MEMBER_004", "이미 사용 중인 로그인 아이디입니다."),
    MEMBER_ALREADY_WITHDRAWN(400, "MEMBER_005", "이미 탈퇴한 회원입니다.");

    private final int status;
    private final String code;
    private final String message;

    MemberErrorCode(int status, String code, String message) {
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
