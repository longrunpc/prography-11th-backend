package com.longrunpc.api.admin.member.dto.request;

public record UpdateMemberRequest(
    String name,
    String phone,
    Long cohortId,
    Long partId,
    Long teamId
) {
}
