package com.longrunpc.api.admin.session.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.longrunpc.domain.session.entity.SessionStatus;

public record UpdateSessionRequest(
    String title,
    LocalDate date,
    LocalTime time,
    String location,
    SessionStatus sessionStatus
) {
}
