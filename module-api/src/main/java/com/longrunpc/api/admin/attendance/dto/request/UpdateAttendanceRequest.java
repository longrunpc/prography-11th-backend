package com.longrunpc.api.admin.attendance.dto.request;

import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출석 수정 요청")
public record UpdateAttendanceRequest(
    @Schema(description = "출석 상태", example = "ABSENT")
    AttendanceStatus status,
    @Schema(description = "지각 분", example = "0")
    Integer lateMinutes,
    @Schema(description = "사유", example = "개인 사정")
    String reason
) {
}
