package com.longrunpc.api.admin.session.dto.request;

import java.time.LocalDate;

import com.longrunpc.domain.session.entity.SessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세션 목록 조회 조건")
public record ReadSessionDetailsRequest(
    @Schema(description = "조회 시작일", example = "2026-03-01")
    LocalDate dateFrom,
    @Schema(description = "조회 종료일", example = "2026-03-31")
    LocalDate dateTo,
    @Schema(description = "세션 상태", example = "SCHEDULED")
    SessionStatus sessionStatus
) {
}
