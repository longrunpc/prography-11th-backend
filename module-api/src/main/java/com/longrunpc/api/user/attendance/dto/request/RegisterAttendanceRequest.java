package com.longrunpc.api.user.attendance.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterAttendanceRequest(
    @NotBlank
    String hashValue,
    @NotNull
    Long memberId
) {
}
