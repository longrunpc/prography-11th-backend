package com.longrunpc.api.admin.attendance.dto.request;

import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 출석 등록 요청")
public record AdminRegisterAttendanceRequest(
    @Schema(description = "세션 ID", example = "10")
    Long sessionId,
    @Schema(description = "멤버 ID", example = "1")
    Long memberId,
    @Schema(description = "출석 상태", example = "LATE")
    AttendanceStatus status,
    @Schema(description = "지각 분", example = "8")
    Integer lateMinutes,
    @Schema(description = "사유", example = "지하철 지연")
    String reason
) {
}
