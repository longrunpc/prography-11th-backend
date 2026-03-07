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
    public static MemberDashboardResponse of(List<MemberInfoResponse> memberInfoResponses, int page, int size, long totalElements, int totalPages) {
        return MemberDashboardResponse.builder()
            .content(memberInfoResponses)
            .page(page)
            .size(size)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .build();
    }
}
