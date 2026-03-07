package com.longrunpc.api.admin.session.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateSessionRequest(
    String title,
    LocalDate date,
    LocalTime time,
    String location
) {
}
