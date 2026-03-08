package com.longrunpc.api.user.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginMemberRequest(
    @NotBlank
    String loginId,
    @NotBlank
    String password
) {
}
