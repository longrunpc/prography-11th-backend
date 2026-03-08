package com.longrunpc.api.admin.cohort.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.cohort.dto.response.CohortDetailResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.Part;
import com.longrunpc.domain.cohort.entity.Team;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.PartRepository;
import com.longrunpc.domain.cohort.repository.TeamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadCohortDetailUsecase {
    private final CohortRepository cohortRepository;
    private final PartRepository partRepository;
    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public CohortDetailResponse execute(Long cohortId) {
        Cohort cohort = cohortRepository.findById(cohortId)
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));

        List<Part> parts = partRepository.findByCohortId(cohortId);
        List<Team> teams = teamRepository.findByCohortId(cohortId);

        return CohortDetailResponse.of(cohort, parts, teams);
    }
}
