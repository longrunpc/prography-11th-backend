package com.longrunpc.api.admin.session.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;

import lombok.Builder;

@Builder
public record SessionDetailResponse(
    Long id,
    Long cohortId,
    String title,
    LocalDate date,
    LocalTime time,
    String location,
    SessionStatus sessionStatus,
    AttendanceSummaryResponse attendanceSummary,
    boolean qrActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static SessionDetailResponse of(Session session, List<Attendance> attendances, boolean qrActive) {
        return SessionDetailResponse.builder()
            .id(session.getId())
            .cohortId(session.getCohort().getId())
            .title(session.getTitle().getValue())
            .date(session.getSessionDate())
            .time(session.getSessionTime())
            .location(session.getSessionLocation().getValue())
            .sessionStatus(session.getSessionStatus())
            .attendanceSummary(AttendanceSummaryResponse.of(attendances))
            .qrActive(qrActive)
            .createdAt(session.getCreatedAt())
            .updatedAt(session.getUpdatedAt())
            .build();
    }
}
