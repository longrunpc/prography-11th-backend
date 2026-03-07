package com.longrunpc.domain.session.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.longrunpc.domain.session.entity.QrCode;

public interface QrCodeRepository extends JpaRepository<QrCode, Long> {
    
}
