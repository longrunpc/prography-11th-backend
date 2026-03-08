package com.longrunpc.api.admin.session.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.api.admin.session.dto.response.QrCodeResponse;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReissueQrCodeUsecase {
    private final QrCodeRepository qrCodeRepository;

    @Transactional
    public QrCodeResponse execute(Long qrCodeId) {
        QrCode qrCode = qrCodeRepository.findById(qrCodeId)
            .orElseThrow(() -> new BusinessException(SessionErrorCode.QR_NOT_FOUND));
            
        qrCode.expire();
        qrCodeRepository.save(qrCode);

        QrCode newQrCode = QrCode.createQrCode(qrCode.getSession());
        QrCode savedNewQrCode = qrCodeRepository.save(newQrCode);

        return QrCodeResponse.of(savedNewQrCode);
    }
    
}
