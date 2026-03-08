package com.longrunpc.api.admin.session.usecase;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.session.dto.response.QrCodeResponse;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.repository.SessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateQrCodeUsecase {
    private final QrCodeRepository qrCodeRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public QrCodeResponse createQrCode(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new BusinessException(SessionErrorCode.SESSION_NOT_FOUND));
        LocalDateTime now = LocalDateTime.now();
        List<QrCode> qrCodes = qrCodeRepository.findBySessionIdAndExpiresAtAfter(sessionId, now);
        
        if (!qrCodes.isEmpty()) {
            throw new BusinessException(SessionErrorCode.QR_ALREADY_ACTIVE);
        }
        QrCode qrCode = QrCode.createQrCode(session);
        QrCode savedQrCode = qrCodeRepository.save(qrCode);

        return QrCodeResponse.of(savedQrCode);
    }
    
}
