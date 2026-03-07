package com.longrunpc.api.admin.session.usecase;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.session.dto.request.CreateSessionRequest;
import com.longrunpc.api.admin.session.dto.response.SessionDetailResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor    
public class CreateSessionUsecase {

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    private final SessionRepository sessionRepository;
    private final QrCodeRepository qrCodeRepository;
    private final CohortRepository cohortRepository;

    @Transactional
    public SessionDetailResponse execute(CreateSessionRequest request) {
        Cohort cohort = cohortRepository.findByGeneration(new Generation(currentGeneration))
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));
        Session session = Session.createSession(
            cohort, 
            new SessionTitle(request.title()), 
            request.date(), 
            request.time(), 
            new SessionLocation(request.location())
        );
        Session savedSession = sessionRepository.save(session);
        
        QrCode qrCode = QrCode.createQrCode(savedSession);
        qrCodeRepository.save(qrCode);

        return SessionDetailResponse.of(savedSession, new ArrayList<>(), true);
    }
    
}
