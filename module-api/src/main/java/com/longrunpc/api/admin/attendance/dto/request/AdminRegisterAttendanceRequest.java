package com.longrunpc.api.admin.attendance.dto.request;

import com.longrunpc.domain.attendance.entity.AttendanceStatus;

public record AdminRegisterAttendanceRequest(
    Long sessionId,
    Long memberId,
    AttendanceStatus status,
    Integer lateMinutes,
    String reason
) {
}
