package com.longrunpc.api.admin.cohort.dto.response;

import java.time.LocalDateTime;

import com.longrunpc.domain.cohort.entity.Cohort;

import lombok.Builder;

@Builder
public record CohortResponse(
    Long id,
    Integer generation,
    String name,
    LocalDateTime createdAt
) {
    public static CohortResponse of(Cohort cohort) {
        return CohortResponse.builder()
            .id(cohort.getId())
            .generation(cohort.getGeneration().getValue())
            .name(cohort.getCohortName().getValue())
            .createdAt(cohort.getCreatedAt())
            .build();
    }
}
