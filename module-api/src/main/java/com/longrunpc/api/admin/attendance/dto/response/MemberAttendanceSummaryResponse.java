package com.longrunpc.api.admin.attendance.dto.response;

import java.util.Map;

import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.cohort.entity.CohortMember;

import lombok.Builder;

@Builder
public record MemberAttendanceSummaryResponse(
    Long memberId,
    String memberName,
    int present,
    int absent,
    int late,
    int totalPenalty,
    int deposit
) {
    public static MemberAttendanceSummaryResponse of(CohortMember cohortMember, Map<AttendanceStatus, Long> attendanceStatusMap, int totalPenalty) {
        return MemberAttendanceSummaryResponse.builder()
            .memberId(cohortMember.getMember().getId())
            .memberName(cohortMember.getMember().getMemberName().getValue())
            .present(attendanceStatusMap.getOrDefault(AttendanceStatus.PRESENT, 0L).intValue())
            .absent(attendanceStatusMap.getOrDefault(AttendanceStatus.ABSENT, 0L).intValue())
            .late(attendanceStatusMap.getOrDefault(AttendanceStatus.LATE, 0L).intValue())
            .totalPenalty(totalPenalty)
            .deposit(cohortMember.getDeposit().getValue())
            .build();
    }
}
