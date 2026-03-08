package com.longrunpc.api.admin.attendance.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.cohort.entity.CohortMember;

import lombok.Builder;

@Builder
public record MemberAttendanceResponse(
    Long memberId,
    String memberName,
    int generation,
    String partName,
    String teamName,
    int deposit,
    int excusedCount,
    List<AdminAttendanceResponse> attendances
) {
    public static MemberAttendanceResponse of(CohortMember cohortMember, List<Attendance> attendances) {
        return MemberAttendanceResponse.builder()
            .memberId(cohortMember.getMember().getId())
            .memberName(cohortMember.getMember().getMemberName().getValue())
            .generation(cohortMember.getCohort().getGeneration().getValue())
            .partName(cohortMember.getPart() != null ? cohortMember.getPart().getPartName().getValue() : null)
            .teamName(cohortMember.getTeam() != null ? cohortMember.getTeam().getTeamName().getValue() : null)
            .deposit(cohortMember.getDeposit().getValue())
            .excusedCount(cohortMember.getExcusedCount().getValue())
            .attendances(attendances.stream().map(AdminAttendanceResponse::of).collect(Collectors.toList()))
            .build();
    }
}
