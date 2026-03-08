package com.longrunpc.common.constant.attendance;

public final class AttendanceConstants {
    private AttendanceConstants() {}

    public static final int LATE_MINUTES_PENALTY_AMOUNT = 1000;
    public static final int MAX_PENALTY_AMOUNT = 10000;

    public static final String REGISTER_PENALTY_DESCRIPTION = "출결 등록 - %s 패널티 %d원";
    public static final String UPDATE_EXCUSED_DESCRIPTION = "출결 수정 - %s 공결 %d원";
    public static final String UPDATE_PENALTY_DESCRIPTION = "출결 수정 - %s 패널티 %d원";
}
