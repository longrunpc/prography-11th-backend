package com.longrunpc.domain.session.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.longrunpc.domain.session.entity.QrCode;

public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    Optional<QrCode> findByHashValue(String hashValue);

    List<QrCode> findBySessionIdAndExpiresAtAfter(Long sessionId, LocalDateTime now);

    List<QrCode> findBySessionIdInAndExpiresAtAfter(List<Long> sessionIds, LocalDateTime now);
}
