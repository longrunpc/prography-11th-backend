package com.longrunpc.api.user.attendance.dto.request;

public record RegisterAttendanceRequest(
    String hashValue,
    Long memberId
) {
}
