package com.longrunpc.api.user.attendance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "출석 등록 요청")
public record RegisterAttendanceRequest(
    @NotBlank
    @Schema(description = "QR 해시값", example = "a3f9f8c7e2d1")
    String hashValue,
    @NotNull
    @Schema(description = "멤버 ID", example = "1")
    Long memberId
) {
}
