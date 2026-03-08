package com.longrunpc.api.admin.session.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSessionRequest(
    @NotBlank
    String title,
    @NotNull
    LocalDate date,
    @NotNull
    LocalTime time,
    @NotBlank
    String location
) {
}
