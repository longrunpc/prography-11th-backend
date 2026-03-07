package com.longrunpc.api.admin.member.dto.response;

import java.time.LocalDateTime;

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
}
