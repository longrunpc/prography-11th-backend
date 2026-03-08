package com.longrunpc.api.user.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record LoginMemberRequest(
    @NotBlank
    @Schema(description = "로그인 아이디", example = "hong123")
    String loginId,
    @NotBlank
    @Schema(description = "비밀번호", example = "Passw0rd!")
    String password
) {
}
