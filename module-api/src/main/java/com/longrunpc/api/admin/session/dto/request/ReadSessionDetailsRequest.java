package com.longrunpc.api.admin.session.dto.request;

import java.time.LocalDate;

import com.longrunpc.domain.session.entity.SessionStatus;

public record ReadSessionDetailsRequest(
    LocalDate dateFrom,
    LocalDate dateTo,
    SessionStatus sessionStatus
) {
}
