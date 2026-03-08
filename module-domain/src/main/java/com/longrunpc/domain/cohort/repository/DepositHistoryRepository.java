package com.longrunpc.domain.cohort.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.longrunpc.domain.cohort.entity.DepositHistory;

public interface DepositHistoryRepository extends JpaRepository<DepositHistory, Long> {
    List<DepositHistory> findByCohortMemberId(Long cohortMemberId);
    List<DepositHistory> findByCohortMemberIdOrderByCreatedAtDesc(Long cohortMemberId);
}
