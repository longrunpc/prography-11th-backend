package com.longrunpc.api.admin.session.usecase;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.api.admin.session.dto.response.SessionDetailResponse;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class CancelSessionUsecase {
    private final SessionRepository sessionRepository;

    @Transactional
    public SessionDetailResponse execute(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new BusinessException(SessionErrorCode.SESSION_NOT_FOUND));

        if (session.isCancelled()) {
            throw new BusinessException(SessionErrorCode.SESSION_ALREADY_CANCELLED);
        }

        session.cancel();

        Session savedSession = sessionRepository.save(session);

        return SessionDetailResponse.of(savedSession, new ArrayList<>(), true);
    }
    
}
