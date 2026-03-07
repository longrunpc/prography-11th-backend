package com.longrunpc.api.adimin.member.dto.request;

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