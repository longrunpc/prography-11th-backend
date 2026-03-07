package com.longrunpc.api.user.session.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.api.user.session.dto.response.SessionResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadValidSessionsUsecase {

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    private final SessionRepository sessionRepository;
    private final CohortRepository cohortRepository;
    
    @Transactional(readOnly = true)
    public List<SessionResponse> execute() {
        Generation generation = new Generation(currentGeneration);
        Cohort cohort = cohortRepository.findByGeneration(generation)
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));

        List<Session> sessions = sessionRepository.findByCohortIdAndSessionStatusNot(cohort.getId(), SessionStatus.CANCELLED);

        return sessions.stream().map(SessionResponse::of).toList();
    }
}
