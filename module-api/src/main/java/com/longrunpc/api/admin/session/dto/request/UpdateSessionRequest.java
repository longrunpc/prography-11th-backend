package com.longrunpc.api.admin.session.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.longrunpc.domain.session.entity.SessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세션 수정 요청")
public record UpdateSessionRequest(
    @Schema(description = "세션 제목", example = "11기 백엔드 스터디 2회차")
    String title,
    @Schema(description = "세션 날짜", example = "2026-03-17")
    LocalDate date,
    @Schema(description = "세션 시작 시간", example = "20:00:00")
    LocalTime time,
    @Schema(description = "세션 장소", example = "온라인 Zoom")
    String location,
    @Schema(description = "세션 상태", example = "SCHEDULED")
    SessionStatus sessionStatus
) {
}
