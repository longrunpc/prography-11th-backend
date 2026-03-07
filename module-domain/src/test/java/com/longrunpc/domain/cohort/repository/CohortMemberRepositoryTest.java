package com.longrunpc.domain.cohort.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.config.QuerydslTestConfig;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;

import jakarta.persistence.EntityManager;

@DataJpaTest
@Import(QuerydslTestConfig.class)
public class CohortMemberRepositoryTest {

    @Autowired
    private CohortMemberRepository cohortMemberRepository;

    @Autowired
    private EntityManager entityManager;

    private Member activeMember;
    private Member withdrawnMember;
    
    @BeforeEach
    void setUp() {
        activeMember = Member.builder()
            .loginId(new LoginId("test@example.com"))
            .password(new Password("password"))
            .memberName(new MemberName("test"))
            .phone(new Phone("01012345678"))
            .role(MemberRole.MEMBER)
            .status(MemberStatus.ACTIVE)
            .build();
        entityManager.persist(activeMember);

        withdrawnMember = Member.builder()
            .loginId(new LoginId("test@example.com"))
            .password(new Password("password"))
            .memberName(new MemberName("test"))
            .phone(new Phone("01012345678"))
            .role(MemberRole.MEMBER)
            .status(MemberStatus.WITHDRAWN)
            .build();
        entityManager.persist(withdrawnMember);

        Cohort cohort = Cohort.builder()
            .generation(new Generation(11))
            .cohortName(new CohortName("11기"))
            .build();
        entityManager.persist(cohort);

        CohortMember cohortMember = CohortMember.createCohortMember(activeMember, cohort, null, null);
        CohortMember withdrawnCohortMember = CohortMember.createCohortMember(withdrawnMember, cohort, null, null);
        entityManager.persist(cohortMember);
        entityManager.persist(withdrawnCohortMember);

        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("MemberStatus 필터링 테스트")
    @Nested
    class FilterByStatusTest {
        @DisplayName("ACTIVE 필터링")
        @Test
        void should_filter_by_active() {
            // when
            List<CohortMember> cohortMembers = cohortMemberRepository.findAllConditions(MemberStatus.ACTIVE, null, null);

            // then
            assertThat(cohortMembers).hasSize(1);
            assertThat(cohortMembers.get(0).getMember().getStatus()).isEqualTo(MemberStatus.ACTIVE);
        }

        @DisplayName("WITHDRAWN 필터링")
        @Test
        void should_filter_by_withdrawn() {
            // when
            List<CohortMember> cohortMembers = cohortMemberRepository.findAllConditions(MemberStatus.WITHDRAWN, null, null);

            // then
            assertThat(cohortMembers).hasSize(1);
            assertThat(cohortMembers.get(0).getMember().getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
        }

        @DisplayName("INACTIVE 필터링")
        @Test
        void should_filter_by_inactive() {
            // when
            List<CohortMember> cohortMembers = cohortMemberRepository.findAllConditions(MemberStatus.INACTIVE, null, null);

            // then
            assertThat(cohortMembers).hasSize(0);
        }
    }
}
