package com.longrunpc.api.admin.member.dto.response;

import java.time.LocalDateTime;

import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;

import lombok.Builder;

@Builder
public record AdminMemberResponse(
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
    public static AdminMemberResponse of(Member member, int generation, String partName, String teamName) {
        return AdminMemberResponse.builder()
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
}
