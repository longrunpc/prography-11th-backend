package com.longrunpc.api.user.member.dto.response;

import java.time.LocalDateTime;

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
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static MemberDetailResponse of(Member member) {
        return MemberDetailResponse.builder()
            .id(member.getId())
            .loginId(member.getLoginId().getValue())
            .name(member.getMemberName().getValue())
            .phone(member.getPhone().getValue())
            .status(member.getStatus())
            .role(member.getRole())
            .createdAt(member.getCreatedAt())
            .updatedAt(member.getUpdatedAt())
            .build();
    }
}
