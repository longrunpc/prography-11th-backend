package com.longrunpc.domain.cohort.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.member.entity.MemberStatus;

public interface CohortMemberRepository extends JpaRepository<CohortMember, Long>, CohortMemberRepositoryCustom {
    @Query("SELECT cm FROM CohortMember cm WHERE cm.member.id = :memberId AND cm.cohort.id = :cohortId")
    Optional<CohortMember> findByMemberIdAndCohortId(@Param("memberId") Long memberId, @Param("cohortId") Long cohortId);
    
    @Query("SELECT cm FROM CohortMember cm" +
            " LEFT JOIN FETCH cm.member" +
            " LEFT JOIN FETCH cm.cohort" +
            " LEFT JOIN FETCH cm.part" +
            " LEFT JOIN FETCH cm.team" +
            " WHERE cm.member.id = :memberId")
    Optional<CohortMember> findDetailByMemberId(@Param("memberId") Long memberId);

    List<CohortMember> findAllConditions(MemberStatus status, String searchType, String searchValue);

    Optional<CohortMember> findByCohortIdAndMemberId(Long cohortId, Long memberId);

    @Query("SELECT cm FROM CohortMember cm" +
            " INNER JOIN FETCH cm.member" +
            " WHERE cm.cohort.id = :cohortId")
    List<CohortMember> findAllByCohortIdWithMember(Long cohortId);
}
