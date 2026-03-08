package com.longrunpc.api.admin.attendance.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.session.entity.Session;

import lombok.Builder;

@Builder
public record SessionAttendanceResponse(
    Long sessionId,
    String sessionTitle,
    List<AdminAttendanceResponse> attendances
) {
    public static SessionAttendanceResponse of(Session session, List<Attendance> attendances) {
        return SessionAttendanceResponse.builder()
            .sessionId(session.getId())
            .sessionTitle(session.getTitle().getValue())
            .attendances(attendances.stream().map(AdminAttendanceResponse::of).collect(Collectors.toList()))
            .build();
    }
}
