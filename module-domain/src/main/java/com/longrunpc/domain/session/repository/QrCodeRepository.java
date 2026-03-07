package com.longrunpc.domain.session.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.longrunpc.domain.session.entity.QrCode;

public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    List<QrCode> findBySessionIdInAndExpiresAtAfter(List<Long> sessionIds, LocalDateTime now);
}
