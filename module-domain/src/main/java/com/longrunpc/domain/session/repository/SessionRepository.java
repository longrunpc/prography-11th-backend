package com.longrunpc.domain.session.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.longrunpc.domain.session.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
