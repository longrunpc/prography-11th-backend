package com.longrunpc.domain.cohort.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.longrunpc.domain.cohort.entity.CohortMember;

public interface CohortMemberRepository extends JpaRepository<CohortMember, Long> {
    @Query("SELECT cm FROM CohortMember cm WHERE cm.member.id = :memberId AND cm.cohort.id = :cohortId")
    Optional<CohortMember> findByMemberIdAndCohortId(Long memberId, Long cohortId);
}
