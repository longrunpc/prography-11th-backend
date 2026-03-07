package com.longrunpc.api.admin.session.usecase;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.api.admin.session.dto.response.SessionDetailResponse;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.api.admin.session.dto.request.UpdateSessionRequest;

@Service
@RequiredArgsConstructor
public class UpdateSessionUsecase {
    private final SessionRepository sessionRepository;

    @Transactional
    public SessionDetailResponse execute(UpdateSessionRequest request, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new BusinessException(SessionErrorCode.SESSION_NOT_FOUND));
            
        if (session.isCancelled()) {
            throw new BusinessException(SessionErrorCode.SESSION_ALREADY_CANCELLED);
        }

        if (request.title() != null) {
            session.changeTitle(new SessionTitle(request.title()));
        }
        if (request.date() != null) {
            session.changeSessionDate(request.date());
        }
        if (request.time() != null) {
            session.changeSessionTime(request.time());
        }
        if (request.location() != null) {
            session.changeSessionLocation(new SessionLocation(request.location()));
        }
        if (request.sessionStatus() != null) {
            session.changeSessionStatus(request.sessionStatus());
        }
        return SessionDetailResponse.of(session, new ArrayList<>(), true);
    }
    
}
