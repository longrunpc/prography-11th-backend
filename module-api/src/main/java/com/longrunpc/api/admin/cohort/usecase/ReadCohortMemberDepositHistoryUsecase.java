package com.longrunpc.api.admin.cohort.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.cohort.dto.response.CohortMemberDepositHistoryResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadCohortMemberDepositHistoryUsecase {

    private final CohortMemberRepository cohortMemberRepository;
    private final DepositHistoryRepository depositHistoryRepository;

    @Transactional(readOnly = true)
    public List<CohortMemberDepositHistoryResponse> execute(Long cohortMemberId) {
        cohortMemberRepository.findById(cohortMemberId)
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_MEMBER_NOT_FOUND));

        List<DepositHistory> depositHistories = depositHistoryRepository.findByCohortMemberIdOrderByCreatedAtDesc(cohortMemberId);

        return depositHistories.stream()
                .map(CohortMemberDepositHistoryResponse::of)
                .toList();
    }
}
