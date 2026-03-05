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

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Builder
    private QrCode(Long id, Session session, QrCodeHashValue hashValue, LocalDateTime expiresAt, boolean isActive) {
        this.id = id;
        this.session = session;
        this.hashValue = hashValue;
        this.expiresAt = expiresAt;
        this.isActive = isActive;
    }

    public static QrCode createQrCode(Session session, QrCodeHashValue hashValue, LocalDateTime expiresAt) {
        return QrCode.builder()
            .session(session)
            .hashValue(hashValue)
            .expiresAt(expiresAt)
            .isActive(true)
            .build();
    }
}
