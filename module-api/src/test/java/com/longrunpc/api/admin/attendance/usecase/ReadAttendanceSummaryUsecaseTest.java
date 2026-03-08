package com.longrunpc.api.admin.attendance.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.api.admin.attendance.dto.response.AdminMemberAttendanceSummaryResponse;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.attendance.vo.Reason;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.cohort.entity.DepositType;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.ExcusedCount;
import com.longrunpc.domain.cohort.vo.Description;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@DisplayName("ReadAttendanceSummaryUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class ReadAttendanceSummaryUsecaseTest {
    @InjectMocks
    private ReadAttendanceSummaryUsecase readAttendanceSummaryUsecase;
    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private CohortMemberRepository cohortMemberRepository;
    @Mock
    private CohortRepository cohortRepository;
    @Mock
    private DepositHistoryRepository depositHistoryRepository;
    @Mock
    private SessionRepository sessionRepository;

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    private Cohort cohort;
    private CohortMember cohortMember1;
    private CohortMember cohortMember2;
    private Attendance attendance1;
    private Attendance attendance2;
    private DepositHistory depositHistory;
    private Member member1;
    private Member member2;
    private Session session;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(readAttendanceSummaryUsecase, "currentGeneration", currentGeneration);
        member1 = Member.builder()
            .id(1L)
            .loginId(new LoginId("test@example.com"))
            .password(new Password("password"))
            .memberName(new MemberName("test"))
            .phone(new Phone("01012345678"))
            .role(MemberRole.MEMBER)
            .status(MemberStatus.ACTIVE)
            .build();
        member2 = Member.builder()
            .id(2L)
            .loginId(new LoginId("test2@example.com"))
            .password(new Password("password2"))
            .memberName(new MemberName("test2"))
            .phone(new Phone("01012345679"))
            .role(MemberRole.MEMBER)
            .status(MemberStatus.ACTIVE)
            .build();
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(currentGeneration))
            .cohortName(new CohortName("11기"))
            .build();
        session = Session.builder()
            .id(1L)
            .cohort(cohort)
            .title(new SessionTitle("test"))
            .sessionDate(LocalDate.now().plusDays(1))
            .sessionTime(LocalTime.now().plusHours(1))
            .sessionLocation(new SessionLocation("강남역"))
            .sessionStatus(SessionStatus.SCHEDULED)
            .build();
        cohortMember1 = CohortMember.builder()
            .id(1L)
            .member(member1)
            .cohort(cohort)
            .part(null)
            .team(null)
            .deposit(new Deposit(100_000))
            .excusedCount(new ExcusedCount(0))
            .build();
        cohortMember2 = CohortMember.builder()
            .id(2L)
            .member(member2)
            .cohort(cohort)
            .part(null)
            .team(null)
            .deposit(new Deposit(100_000))
            .excusedCount(new ExcusedCount(0))
            .build();
        attendance1 = Attendance.builder()
            .id(1L)
            .member(member1)
            .session(session)
            .qrCode(null)
            .attendanceStatus(AttendanceStatus.PRESENT)
            .lateMinutes(new LateMinutes(5))
            .penaltyAmount(new PenaltyAmount(10000))
            .reason(new Reason("지각"))
            .checkedInAt(LocalDateTime.now())
            .build();
        attendance2 = Attendance.builder()
            .id(2L)
            .member(member2)
            .session(session)
            .qrCode(null)
            .attendanceStatus(AttendanceStatus.ABSENT)
            .lateMinutes(new LateMinutes(0))
            .penaltyAmount(new PenaltyAmount(0))
            .reason(new Reason("test"))
            .checkedInAt(LocalDateTime.now())
            .build();
        depositHistory = DepositHistory.builder()
            .id(1L)
            .cohortMember(cohortMember1)
            .depositType(DepositType.PENALTY)
            .amount(100_000)
            .balanceAfter(100_000)
            .description(new Description("초기 입금"))
            .build();
    }
    
    @DisplayName("출결 요약 조회 성공")
    @Test
    void should_get_attendance_summary_when_valid_input() {
        // given
        given(cohortRepository.findByGeneration(new Generation(currentGeneration))).willReturn(Optional.of(cohort));
        given(sessionRepository.findById(1L)).willReturn(Optional.of(session));
        given(cohortMemberRepository.findAllByCohortIdWithMember(1L)).willReturn(List.of(cohortMember1, cohortMember2));
        given(attendanceRepository.findAllByMemberIdIn(List.of(1L, 2L))).willReturn(List.of(attendance1, attendance2));

        // when
        List<AdminMemberAttendanceSummaryResponse> result = readAttendanceSummaryUsecase.execute(1L);

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @DisplayName("출결 요약 조회 실패 - 일정 존재하지 않음")
    @Test
    void should_fail_to_get_attendance_summary_when_session_not_found() {
        // given
        given(cohortRepository.findByGeneration(new Generation(currentGeneration))).willReturn(Optional.of(cohort));
        given(sessionRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readAttendanceSummaryUsecase.execute(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.SESSION_NOT_FOUND.getMessage());
    }
}
