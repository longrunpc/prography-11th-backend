package com.longrunpc.api.admin.session.dto.response;

import java.util.List;

import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;

import lombok.Builder;

@Builder
public record AttendanceSummaryResponse(
    int present,
    int absent,
    int late,
    int excused,
    int total
) {
    public static AttendanceSummaryResponse of(List<Attendance> attendances) {
        return AttendanceSummaryResponse.builder()
            .present((int) attendances.stream().filter(a -> a.getAttendanceStatus() == AttendanceStatus.PRESENT).count())
            .absent((int) attendances.stream().filter(a -> a.getAttendanceStatus() == AttendanceStatus.ABSENT).count())
            .late((int) attendances.stream().filter(a -> a.getAttendanceStatus() == AttendanceStatus.LATE).count())
            .excused((int) attendances.stream().filter(a -> a.getAttendanceStatus() == AttendanceStatus.EXCUSED).count())
            .total((int) attendances.size())
            .build();
    }
}