package com.longrunpc.api.admin.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "멤버 등록 요청")
public record RegisterMemberRequest(
    @NotBlank
    @Schema(description = "로그인 아이디", example = "hong123")
    String loginId,
    @NotBlank
    @Schema(description = "비밀번호", example = "Passw0rd!")
    String password,
    @NotBlank
    @Schema(description = "멤버 이름", example = "홍길동")
    String memberName,
    @NotBlank
    @Schema(description = "전화번호", example = "01012345678")
    String phone,
    @NotBlank
    @Schema(description = "기수 ID", example = "11")
    Long cohortId,
    @Schema(description = "파트 ID", example = "2")
    Long partId,
    @Schema(description = "팀 ID", example = "5")
    Long teamId
) {
}