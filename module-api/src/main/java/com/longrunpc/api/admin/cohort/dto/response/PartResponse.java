package com.longrunpc.api.admin.cohort.dto.response;

import com.longrunpc.domain.cohort.entity.Part;

import lombok.Builder;

@Builder
public record PartResponse(
    Long id,
    String name
) {
    public static PartResponse of(Part part) {
        return PartResponse.builder()
            .id(part.getId())
            .name(part.getPartName().getValue())
            .build();
    }
}
