package com.longrunpc.domain.cohort.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.longrunpc.domain.cohort.entity.Cohort;

public interface CohortRepository extends JpaRepository<Cohort, Long> {
}
