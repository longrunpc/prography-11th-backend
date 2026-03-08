package com.longrunpc.api.user.attendance.dto.response;

import java.time.LocalDateTime;

import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;

import lombok.Builder;

@Builder
public record AttendanceResponse(
    Long id,
    Long sessionId,
    String sessionTitle,
    AttendanceStatus status,
    Integer lateMinutes,
    Integer penaltyAmount,
    String reason,
    LocalDateTime checkedInAt,
    LocalDateTime createdAt
) {
    public static AttendanceResponse of(Attendance attendance) {
        return AttendanceResponse.builder()
            .id(attendance.getId())
            .sessionId(attendance.getSession().getId())
            .sessionTitle(attendance.getSession().getTitle().getValue())
            .status(attendance.getAttendanceStatus())
            .lateMinutes(attendance.getLateMinutes().getValue())
            .penaltyAmount(attendance.getPenaltyAmount().getValue())
            .reason(attendance.getReason().getValue())
            .checkedInAt(attendance.getCheckedInAt())
            .createdAt(attendance.getCreatedAt())
            .build();
    }
}
