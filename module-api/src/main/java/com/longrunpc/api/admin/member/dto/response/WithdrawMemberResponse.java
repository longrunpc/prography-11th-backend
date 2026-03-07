package com.longrunpc.api.admin.member.dto.response;

import java.time.LocalDateTime;

import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.entity.MemberStatus;

import lombok.Builder;

@Builder
public record WithdrawMemberResponse(
    Long id,
    String loginId,
    String name,
    MemberStatus status,
    LocalDateTime updatedAt
) {
    public static WithdrawMemberResponse of(Member member) {
        return WithdrawMemberResponse.builder()
            .id(member.getId())
            .loginId(member.getLoginId().getValue())
            .name(member.getMemberName().getValue())
            .status(member.getStatus())
            .updatedAt(member.getUpdatedAt())
            .build();
    }
}
