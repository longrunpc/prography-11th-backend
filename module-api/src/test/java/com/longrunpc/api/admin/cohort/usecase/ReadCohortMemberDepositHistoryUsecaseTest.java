package com.longrunpc.api.admin.cohort.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.cohort.entity.DepositType;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.api.admin.cohort.dto.response.CohortMemberDepositHistoryResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.Description;
import com.longrunpc.domain.cohort.vo.ExcusedCount;
import java.util.List;

@DisplayName("ReadCohortMemberDepositHistoryUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class ReadCohortMemberDepositHistoryUsecaseTest {
    @InjectMocks
    private ReadCohortMemberDepositHistoryUsecase readCohortMemberDepositHistoryUsecase;
    @Mock
    private CohortMemberRepository cohortMemberRepository;
    @Mock
    private DepositHistoryRepository depositHistoryRepository;

    private CohortMember cohortMember;
    private List<DepositHistory> depositHistories;
    private Member member;
    private Cohort cohort;

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .id(1L)
            .loginId(new LoginId("test@example.com"))
            .password(new Password("password"))
            .memberName(new MemberName("test"))
            .phone(new Phone("01012345678"))
            .role(MemberRole.MEMBER)
            .status(MemberStatus.ACTIVE)
            .build();
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(11))
            .cohortName(new CohortName("11기"))
            .build();
        cohortMember = CohortMember.builder()
            .id(1L)
            .member(member)
            .cohort(cohort)
            .part(null)
            .team(null)
            .deposit(new Deposit(100_000))
            .excusedCount(new ExcusedCount(0))
            .build();
        depositHistories = List.of(
            DepositHistory.builder()
                .id(1L)
                .cohortMember(cohortMember)
                .attendance(null)
                .depositType(DepositType.PENALTY)
                .amount(100_000)
                .balanceAfter(100_000)
                .description(new Description("test"))
                .build(),
            DepositHistory.builder()
                .id(2L)
                .cohortMember(cohortMember)
                .attendance(null)
                .depositType(DepositType.PENALTY)
                .amount(100_000)
                .balanceAfter(100_000)
                .description(new Description("test"))
                .build());
    }

    @DisplayName("출결 요약 조회 성공")
    @Test
    void should_get_attendance_summary_when_valid_input() {
        // given
        given(cohortMemberRepository.findById(1L)).willReturn(Optional.of(cohortMember));
        given(depositHistoryRepository.findByCohortMemberIdOrderByCreatedAtDesc(1L)).willReturn(depositHistories);

        // when
        List<CohortMemberDepositHistoryResponse> result = readCohortMemberDepositHistoryUsecase.execute(1L);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).cohortMemberId()).isEqualTo(1L);
        assertThat(result.get(0).depositType()).isEqualTo(DepositType.PENALTY);
        assertThat(result.get(0).amount()).isEqualTo(100_000);
        assertThat(result.get(0).balanceAfter()).isEqualTo(100_000);
        assertThat(result.get(0).attendanceId()).isNull();
        assertThat(result.get(0).description()).isEqualTo("test");
    }

    @DisplayName("출결 요약 조회 실패 - 존재하지 않는 기수 회원")
    @Test
    void should_fail_to_get_attendance_summary_when_cohort_member_not_found() {
        // given
        given(cohortMemberRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readCohortMemberDepositHistoryUsecase.execute(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.COHORT_MEMBER_NOT_FOUND.getMessage());
    }
}
