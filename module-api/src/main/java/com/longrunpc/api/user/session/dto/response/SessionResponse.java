package com.longrunpc.api.user.session.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;

import lombok.Builder;

@Builder
public record SessionResponse(
    Long id,
    String title,
    LocalDate date,
    LocalTime time,
    String location,
    SessionStatus sessionStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static SessionResponse of(Session session) {
        return SessionResponse.builder()
            .id(session.getId())
            .title(session.getTitle().getValue())
            .date(session.getSessionDate())
            .time(session.getSessionTime())
            .location(session.getSessionLocation().getValue())
            .sessionStatus(session.getSessionStatus())
            .createdAt(session.getCreatedAt())
            .updatedAt(session.getUpdatedAt())
            .build();
    }
}
