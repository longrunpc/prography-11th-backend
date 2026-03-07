package com.longrunpc.api.admin.cohort.dto.response;

import com.longrunpc.domain.cohort.entity.Team;

import lombok.Builder;

@Builder
public record TeamResponse(
    Long id,
    String name
) {
    public static TeamResponse of(Team team) {
        return TeamResponse.builder()
            .id(team.getId())
            .name(team.getTeamName().getValue())
            .build();
    }
    
}
