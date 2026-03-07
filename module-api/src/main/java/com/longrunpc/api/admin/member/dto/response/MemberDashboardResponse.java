package com.longrunpc.api.admin.member.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record MemberDashboardResponse(
    List<MemberInfoResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
}
