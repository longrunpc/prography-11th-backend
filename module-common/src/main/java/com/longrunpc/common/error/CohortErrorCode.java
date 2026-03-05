package com.longrunpc.common.error;

public enum CohortErrorCode implements ErrorCode {

    COHORT_NOT_FOUND(404, "COHORT_001", "기수를 찾을 수 없습니다."),
    PART_NOT_FOUND(404, "COHORT_002", "파트를 찾을 수 없습니다."),
    TEAM_NOT_FOUND(404, "COHORT_003", "팀을 찾을 수 없습니다."),
    COHORT_MEMBER_NOT_FOUND(404, "COHORT_004", "기수 회원 정보를 찾을 수 없습니다."),
    EXCUSE_LIMIT_EXCEEDED(400, "COHORT_006", "공결 횟수를 초과했습니다 (최대 3회)."),
    DEPOSIT_INSUFFICIENT(400, "COHORT_005", "보증금 잔액이 부족합니다.");

    private final int status;
    private final String code;
    private final String message;

    CohortErrorCode(int status, String code, String message) {
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
