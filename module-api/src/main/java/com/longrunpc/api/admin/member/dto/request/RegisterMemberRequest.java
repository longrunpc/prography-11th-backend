package com.longrunpc.api.admin.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterMemberRequest(
    @NotBlank
    String loginId,
    @NotBlank
    String password,
    @NotBlank
    String memberName,
    @NotBlank
    String phone,
    @NotBlank
    Long cohortId,
    Long partId,
    Long teamId
) {
}