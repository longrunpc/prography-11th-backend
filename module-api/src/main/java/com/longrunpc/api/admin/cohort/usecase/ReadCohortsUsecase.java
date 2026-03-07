package com.longrunpc.api.admin.cohort.usecase;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.cohort.dto.response.CohortResponse;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.repository.CohortRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadCohortsUsecase {

    private final CohortRepository cohortRepository;

    @Transactional(readOnly = true)
    public List<CohortResponse> execute() {
        List<Cohort> cohorts = cohortRepository.findAll();

        return cohorts.stream()
            .map(CohortResponse::of)
            .collect(Collectors.toList());
    }
}
