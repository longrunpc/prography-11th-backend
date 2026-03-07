package com.longrunpc.api.user.member.dto.request;

public record LoginMemberRequest(
    String loginId,
    String password
) {
}
