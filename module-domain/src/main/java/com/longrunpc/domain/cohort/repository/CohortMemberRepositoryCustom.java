package com.longrunpc.domain.cohort.repository;

import java.util.List;

import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.member.entity.MemberStatus;

public interface CohortMemberRepositoryCustom {
    List<CohortMember> findAllConditions(MemberStatus status, String searchType, String searchValue);
}
