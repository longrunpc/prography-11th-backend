package com.longrunpc.api.admin.cohort.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.Part;
import com.longrunpc.domain.cohort.entity.Team;

import lombok.Builder;

@Builder
public record CohortDetailResponse(
    Long id,
    Integer generation,
    String name,
    List<PartResponse> parts,
    List<TeamResponse> teams,
    LocalDateTime createdAt
) {
    public static CohortDetailResponse of(Cohort cohort, List<Part> parts, List<Team> teams) {
        return CohortDetailResponse.builder()
            .id(cohort.getId())
            .generation(cohort.getGeneration().getValue())
            .name(cohort.getCohortName().getValue())
            .parts(parts.stream().map(PartResponse::of).collect(Collectors.toList()))
            .teams(teams.stream().map(TeamResponse::of).collect(Collectors.toList()))
            .createdAt(cohort.getCreatedAt())
            .build();
    }
}
