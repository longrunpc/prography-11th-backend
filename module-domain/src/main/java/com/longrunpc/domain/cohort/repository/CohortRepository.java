package com.longrunpc.domain.cohort.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.vo.Generation;

public interface CohortRepository extends JpaRepository<Cohort, Long> {
    
    Optional<Cohort> findByGeneration(Generation generation);
}
