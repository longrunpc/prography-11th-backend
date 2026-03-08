package com.longrunpc.domain.session.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.longrunpc.domain.session.entity.QrCode;

public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    @Query("SELECT q FROM QrCode q WHERE q.hashValue.value = :hashValue")
    Optional<QrCode> findByHashValue(@Param("hashValue") String hashValue);

    List<QrCode> findBySessionIdAndExpiresAtAfter(Long sessionId, LocalDateTime now);

    List<QrCode> findBySessionIdInAndExpiresAtAfter(List<Long> sessionIds, LocalDateTime now);
}
