package com.longrunpc.domain.cohort.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.longrunpc.domain.cohort.entity.CohortMember;

public interface CohortMemberRepository extends JpaRepository<CohortMember, Long> {
    @Query("SELECT cm FROM CohortMember cm WHERE cm.member.id = :memberId AND cm.cohort.id = :cohortId")
    Optional<CohortMember> findByMemberIdAndCohortId(Long memberId, Long cohortId);
    
    @Query("SELECT cm FROM CohortMember cm" +
            " LEFT JOIN FETCH cm.member" +
            " LEFT JOIN FETCH cm.cohort" +
            " LEFT JOIN FETCH cm.part" +
            " LEFT JOIN FETCH cm.team" +
            " WHERE cm.member.id = :memberId")
    Optional<CohortMember> findDetailByMemberId(Long memberId);
}
