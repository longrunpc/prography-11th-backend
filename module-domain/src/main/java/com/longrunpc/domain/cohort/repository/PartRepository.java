package com.longrunpc.domain.cohort.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.longrunpc.domain.cohort.entity.Part;

public interface PartRepository extends JpaRepository<Part, Long> {
}
