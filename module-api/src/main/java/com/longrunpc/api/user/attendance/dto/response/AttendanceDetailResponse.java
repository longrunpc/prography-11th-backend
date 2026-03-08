package com.longrunpc.api.user.attendance.dto.response;

import java.time.LocalDateTime;

import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;

import lombok.Builder;

@Builder
public record AttendanceDetailResponse(
    Long id,
    Long sessionId,
    Long memberId,
    AttendanceStatus status,
    Integer lateMinutes,
    Integer penaltyAmount,
    String reason,
    LocalDateTime checkedInAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static AttendanceDetailResponse of(Attendance attendance) {
        return AttendanceDetailResponse.builder()
            .id(attendance.getId())
            .sessionId(attendance.getSession().getId())
            .memberId(attendance.getMember().getId())
            .status(attendance.getAttendanceStatus())
            .lateMinutes((attendance.getLateMinutes() != null) ? attendance.getLateMinutes().getValue() : null)
            .penaltyAmount(attendance.getPenaltyAmount().getValue())
            .reason(attendance.getReason().getValue())
            .checkedInAt(attendance.getCheckedInAt())
            .createdAt(attendance.getCreatedAt())
            .updatedAt(attendance.getUpdatedAt())
            .build();
    }
}
