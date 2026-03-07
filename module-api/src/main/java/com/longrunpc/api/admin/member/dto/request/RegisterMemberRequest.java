package com.longrunpc.api.admin.member.dto.request;

public record RegisterMemberRequest(
    String loginId,
    String password,
    String memberName,
    String phone,
    Long cohortId,
    Long partId,
    Long teamId
) {
}