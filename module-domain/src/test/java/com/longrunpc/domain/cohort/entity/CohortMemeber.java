package com.longrunpc.domain.cohort.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.ExcusedCount;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.PartName;
import com.longrunpc.domain.cohort.vo.TeamName;
import com.longrunpc.common.constant.cohort.CohortConstants;

@DisplayName("CohortMember 엔티티 테스트")
public class CohortMemeber {
    private Member member;
    private Cohort cohort;
    private Part part;
    private Team team;

    @BeforeEach
    void setUp() {
        //given
        member = Member.builder()
            .loginId(new LoginId("test@example.com"))
            .password(new Password("password"))
            .name(new MemberName("test"))
            .phone(new Phone("01012345678"))
            .role(MemberRole.USER)
            .status(MemberStatus.ACTIVE)
            .build();
        cohort = Cohort.builder()
            .generation(new Generation("2026"))
            .build();
        part = Part.builder()
            .name(new PartName("test"))
            .cohort(cohort)
            .build();
        team = Team.builder()
            .name(new TeamName("test"))
            .cohort(cohort)
            .build();
    }
    
    @DisplayName("createCohortMember 메서드 테스트")
    @Nested
    class CreateCohortMemberTest {

        @DisplayName("모든 필드 입력 시 CohortMember 생성")
        @Test
        void should_create_cohort_member_when_valid_input() {
            // given
            // when
            CohortMember cohortMember = CohortMember.createCohortMember(member, cohort, part, team);

            // then
            assertThat(cohortMember.getMember()).isEqualTo(member);
            assertThat(cohortMember.getCohort()).isEqualTo(cohort);
            assertThat(cohortMember.getPart()).isEqualTo(part);
            assertThat(cohortMember.getTeam()).isEqualTo(team);
            assertThat(cohortMember.getDeposit()).isEqualTo(new Deposit(CohortConstants.INITIAL_DEPOSIT));
            assertThat(cohortMember.getExcusedCount()).isEqualTo(new ExcusedCount(CohortConstants.INITIAL_EXCUSED_COUNT));
        }

        @DisplayName("team 필드 null 시 CohortMember 생성")
        @Test
        void should_create_cohort_member_when_team_is_null() {
            // when
            CohortMember cohortMember = CohortMember.createCohortMember(member, cohort, part, null);
            // then
            assertThat(cohortMember.getTeam()).isNull();
        }

        @DisplayName("part 필드 null 시 CohortMember 생성")
        @Test
        void should_create_cohort_member_when_part_is_null() {
            // when
            CohortMember cohortMember = CohortMember.createCohortMember(member, cohort, null, team);
            // then
            assertThat(cohortMember.getPart()).isNull();
        }

        @DisplayName("cohort 필드 null 시 예외 발생")
        @Test
        void should_create_cohort_member_when_cohort_is_null() {
            // when & then
            assertThatThrownBy(() -> CohortMember.createCohortMember(member, null, part, team))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("isDepositEnough 메서드 테스트")
    @Nested
    class IsDepositEnoughTest {
        @DisplayName("deposit 잔액 충분 시 true 반환")
        @Test
        void should_return_true_when_deposit_is_enough() {
            // given
            CohortMember cohortMember = CohortMember.createCohortMember(member, cohort, part, team);
            // when
            boolean result = cohortMember.isDepositEnough(CohortConstants.INITIAL_DEPOSIT);
            // then
            assertThat(result).isTrue();
        }

        @DisplayName("deposit 잔액 부족 시 false 반환")
        @Test
        void should_return_false_when_deposit_is_not_enough() {
            // given
            CohortMember cohortMember = CohortMember.createCohortMember(member, cohort, part, team);
            // when
            boolean result = cohortMember.isDepositEnough(CohortConstants.INITIAL_DEPOSIT + 1);
            // then
            assertThat(result).isFalse();
        }
    }

    @DisplayName("increaseDeposit 메서드 테스트")
    @Nested
    class IncreaseDepositTest {
        @DisplayName("deposit 증가 시 정상 작동")
        @Test
        void should_increase_deposit_when_valid_input() {
            // given
            CohortMember cohortMember = CohortMember.createCohortMember(member, cohort, part, team);
            // when
            cohortMember.increaseDeposit(100_000);
            // then
            assertThat(cohortMember.getDeposit()).isEqualTo(new Deposit(CohortConstants.INITIAL_DEPOSIT + 100_000));
        }

        @DisplayName("음수 입력 시 예외 발생")
        @Test
        void should_throw_exception_when_negative_input() {
            // given
            CohortMember cohortMember = CohortMember.createCohortMember(member, cohort, part, team);
            // when & then
            assertThatThrownBy(() -> cohortMember.increaseDeposit(-1))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("decreaseDeposit 메서드 테스트")
    @Nested
    class DecreaseDepositTest {
        @DisplayName("deposit 감소 시 정상 작동")
        @Test
        void should_decrease_deposit_when_valid_input() {
            // given
            CohortMember cohortMember = CohortMember.createCohortMember(member, cohort, part, team);
            // when
            cohortMember.decreaseDeposit(100_000);
            // then
            assertThat(cohortMember.getDeposit()).isEqualTo(new Deposit(0));
        }

        @DisplayName("음수 입력 시 예외 발생")
        @Test
        void should_throw_exception_when_negative_input() {
            // given
            CohortMember cohortMember = CohortMember.createCohortMember(member, cohort, part, team);
            // when & then
            assertThatThrownBy(() -> cohortMember.decreaseDeposit(-1))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("increaseExcusedCount 메서드 테스트")
    @Nested
    class IncreaseExcusedCountTest {
        @DisplayName("공결 횟수 증가 시 정상 작동")
        @Test
        void should_increase_excused_count_when_valid_input() {
            // given
            CohortMember cohortMember = CohortMember.createCohortMember(member, cohort, part, team);
            // when
            cohortMember.increaseExcusedCount();
            // then
            assertThat(cohortMember.getExcusedCount()).isEqualTo(new ExcusedCount(1));
        }

        @DisplayName("공결 횟수 초과 시 예외 발생")
        @Test
        void should_throw_exception_when_invalid_input() {
            // given
            CohortMember cohortMember = CohortMember.builder()
                .id(1L)
                .member(member)
                .cohort(cohort)
                .part(part)
                .team(team)
                .deposit(new Deposit(CohortConstants.INITIAL_DEPOSIT))
                .excusedCount(new ExcusedCount(CohortConstants.MAX_EXCUSED_COUNT))
                .build();

            // when & then
            assertThatThrownBy(() -> cohortMember.increaseExcusedCount())
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
