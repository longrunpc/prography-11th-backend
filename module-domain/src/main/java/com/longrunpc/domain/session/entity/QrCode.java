package com.longrunpc.domain.session.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;

import com.longrunpc.common.constant.qrCode.QrCodeConstants;
import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.session.vo.QrCodeHashValue;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

@Entity
@Table(name = "qr_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QrCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;
    
    @Embedded
    private QrCodeHashValue hashValue;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private QrCode(Long id, Session session, QrCodeHashValue hashValue, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.session = session;
        this.hashValue = hashValue;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static QrCode createQrCode(Session session) {
        if (session == null) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT);
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(QrCodeConstants.EXPIRATION_HOURS);
        QrCodeHashValue hashValue = new QrCodeHashValue(QrCodeHashValue.generate());

        return QrCode.builder()
            .session(session)
            .hashValue(hashValue)
            .expiresAt(expiresAt)
            .createdAt(now)
            .build();
    }

    public void expire() {
        this.expiresAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
