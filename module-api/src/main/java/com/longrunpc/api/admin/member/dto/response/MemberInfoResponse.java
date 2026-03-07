package com.longrunpc.api.admin.member.dto.response;

import java.time.LocalDateTime;

import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;

import lombok.Builder;

@Builder
public record MemberInfoResponse(
    Long id,
    String loginId,
    String name,
    String phone,
    MemberStatus status,
    MemberRole role,
    int generation,
    String partName,
    String teamName,
    Integer deposit,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static MemberInfoResponse of(CohortMember cohortMember) {
        return MemberInfoResponse.builder()
            .id(cohortMember.getMember().getId())
            .loginId(cohortMember.getMember().getLoginId().getValue())
            .name(cohortMember.getMember().getMemberName().getValue())
            .phone(cohortMember.getMember().getPhone().getValue())
            .status(cohortMember.getMember().getStatus())
            .role(cohortMember.getMember().getRole())
            .generation(cohortMember.getCohort().getGeneration().getValue())
            .partName(cohortMember.getPart() != null ? cohortMember.getPart().getPartName().getValue() : null)
            .teamName(cohortMember.getTeam() != null ? cohortMember.getTeam().getTeamName().getValue() : null)
            .deposit(cohortMember.getDeposit() != null ? cohortMember.getDeposit().getValue() : null)
            .createdAt(cohortMember.getMember().getCreatedAt())
            .updatedAt(cohortMember.getMember().getUpdatedAt())
            .build();
    }
}
