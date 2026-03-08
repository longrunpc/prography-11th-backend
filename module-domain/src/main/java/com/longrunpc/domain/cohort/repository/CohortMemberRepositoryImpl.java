package com.longrunpc.domain.cohort.repository;

import static com.longrunpc.domain.cohort.entity.QCohortMember.cohortMember;
import static com.longrunpc.domain.member.entity.QMember.member;
import static com.longrunpc.domain.cohort.entity.QCohort.cohort;
import static com.longrunpc.domain.cohort.entity.QPart.part;
import static com.longrunpc.domain.cohort.entity.QTeam.team;


import org.springframework.stereotype.Repository;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.longrunpc.common.constant.cohort.CohortConstants;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.member.entity.MemberStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CohortMemberRepositoryImpl implements CohortMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CohortMember> findAllConditions(MemberStatus status, String searchType, String searchValue) {
        return queryFactory
            .selectFrom(cohortMember)
            .leftJoin(cohortMember.member, member).fetchJoin()
            .leftJoin(cohortMember.cohort, cohort).fetchJoin()
            .leftJoin(cohortMember.part, part).fetchJoin()
            .leftJoin(cohortMember.team, team).fetchJoin()
            .where(statusEq(status), searchTypeEq(searchType, searchValue))
            .orderBy(cohortMember.id.asc())
            .fetch();   
    }
    
    private BooleanExpression statusEq(MemberStatus status) {
        return status != null ? member.status.eq(status) : null;
    }

    private BooleanExpression searchTypeEq(String searchType, String searchValue) {
        if (searchType == null || searchValue == null) {
            return null;
        }
        if (searchType.equalsIgnoreCase(CohortConstants.SEARCH_TYPE_NAME)) {
            return member.memberName.value.containsIgnoreCase(searchValue);
        } 
        if (searchType.equalsIgnoreCase(CohortConstants.SEARCH_TYPE_LOGIN_ID)) {
            return member.loginId.value.containsIgnoreCase(searchValue);
        }
        if (searchType.equalsIgnoreCase(CohortConstants.SEARCH_TYPE_PHONE)) {
            return member.phone.value.contains(searchValue);
        }
        return null;
    }
}
