package com.longrunpc.api.admin.session.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "세션 생성 요청")
public record CreateSessionRequest(
    @NotBlank
    @Schema(description = "세션 제목", example = "11기 백엔드 스터디 1회차")
    String title,
    @NotNull
    @Schema(description = "세션 날짜", example = "2026-03-10")
    LocalDate date,
    @NotNull
    @Schema(description = "세션 시작 시간", example = "19:30:00")
    LocalTime time,
    @NotBlank
    @Schema(description = "세션 장소", example = "강남 위워크 3층")
    String location
) {
}
