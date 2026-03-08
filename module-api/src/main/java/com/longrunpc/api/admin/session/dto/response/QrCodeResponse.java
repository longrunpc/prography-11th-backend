package com.longrunpc.api.admin.session.dto.response;

import java.time.LocalDateTime;

import com.longrunpc.domain.session.entity.QrCode;

import lombok.Builder;

@Builder
public record QrCodeResponse(
    Long id,
    Long sessionId,
    String hashValue,
    LocalDateTime createdAt,
    LocalDateTime expiresAt
) {
    public static QrCodeResponse of(QrCode qrCode) {
        return QrCodeResponse.builder()
            .id(qrCode.getId())
            .sessionId(qrCode.getSession().getId())
            .hashValue(qrCode.getHashValue().getValue())
            .createdAt(qrCode.getCreatedAt())
            .expiresAt(qrCode.getExpiresAt())
            .build();
    }
}
