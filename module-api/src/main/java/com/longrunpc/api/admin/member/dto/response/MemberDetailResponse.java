package com.longrunpc.api.admin.member.dto.response;

import java.time.LocalDateTime;

import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;

import lombok.Builder;

@Builder
public record MemberDetailResponse(
    Long id,
    String loginId,
    String name,
    String phone,
    MemberStatus status,
    MemberRole role,
    int generation,
    String partName,
    String teamName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static MemberDetailResponse of(Member member, int generation, String partName, String teamName) {
        return MemberDetailResponse.builder()
            .id(member.getId())
            .loginId(member.getLoginId().getValue())
            .name(member.getMemberName().getValue())
            .phone(member.getPhone().getValue())
            .status(member.getStatus())
            .role(member.getRole())
            .generation(generation)
            .partName(partName)
            .teamName(teamName)
            .createdAt(member.getCreatedAt())
            .updatedAt(member.getUpdatedAt())
            .build();
    }

    public static MemberDetailResponse of(CohortMember cohortMember) {
        return of(
            cohortMember.getMember(),
            cohortMember.getCohort().getGeneration().getValue(),
            cohortMember.getPart() != null ? cohortMember.getPart().getPartName().getValue() : null,
            cohortMember.getTeam() != null ? cohortMember.getTeam().getTeamName().getValue() : null
        );
    }
}
