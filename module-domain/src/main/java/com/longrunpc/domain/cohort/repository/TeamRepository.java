package com.longrunpc.domain.cohort.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.longrunpc.domain.cohort.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
