package com.longrunpc.api.admin.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "멤버 수정 요청")
public record UpdateMemberRequest(
    @Schema(description = "멤버 이름", example = "홍길동")
    String name,
    @Schema(description = "전화번호", example = "01098765432")
    String phone,
    @Schema(description = "기수 ID", example = "11")
    Long cohortId,
    @Schema(description = "파트 ID", example = "1")
    Long partId,
    @Schema(description = "팀 ID", example = "3")
    Long teamId
) {
}
