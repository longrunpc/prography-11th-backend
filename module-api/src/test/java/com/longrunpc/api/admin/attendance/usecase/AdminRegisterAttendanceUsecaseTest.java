package com.longrunpc.api.admin.attendance.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import com.longrunpc.api.admin.attendance.dto.request.AdminRegisterAttendanceRequest;
import com.longrunpc.api.admin.attendance.dto.response.AdminAttendanceResponse;
import com.longrunpc.common.constant.attendance.AttendanceConstants;
import com.longrunpc.common.error.AttendanceErrorCode;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.ExcusedCount;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.attendance.vo.Reason;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.entity.SessionStatus;

@DisplayName("AdminRegisterAttendanceUsecase 테스트")
@ExtendWith(MockitoExtension.class) 
public class AdminRegisterAttendanceUsecaseTest {

    @InjectMocks
    private AdminRegisterAttendanceUsecase adminRegisterAttendanceUsecase;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private CohortMemberRepository cohortMemberRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private DepositHistoryRepository depositHistoryRepository;

    private Cohort cohort;
    private Session session;
    private Member member;
    private CohortMember cohortMember;
    private Attendance attendance;
    private DepositHistory depositHistory;

    @BeforeEach
    void setUp() {
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(11))
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
        member = Member.builder()
            .id(1L)
            .loginId(new LoginId("test@example.com"))
            .password(new Password("password"))
            .memberName(new MemberName("test"))
            .phone(new Phone("01012345678"))
            .role(MemberRole.MEMBER)
            .status(MemberStatus.ACTIVE)
            .build();
        cohortMember = CohortMember.builder()
            .id(1L)
            .cohort(cohort)
            .member(member)
            .deposit(new Deposit(100000))
            .excusedCount(new ExcusedCount(0))
            .build();
        attendance = Attendance.builder()
            .id(1L)
            .session(session)
            .member(member)
            .attendanceStatus(AttendanceStatus.LATE)
            .lateMinutes(new LateMinutes(10))
            .penaltyAmount(new PenaltyAmount(10 * AttendanceConstants.LATE_MINUTES_PENALTY_AMOUNT))
            .reason(new Reason("test"))
            .checkedInAt(null)
            .build();
    }

    @DisplayName("출결 등록 성공")
    @Test
    void should_register_attendance_when_valid_input() {
        // given
        AdminRegisterAttendanceRequest request = new AdminRegisterAttendanceRequest(
            session.getId(),
            member.getId(),
            AttendanceStatus.LATE,
            10,
            "지각"
        );

        given(sessionRepository.findById(session.getId())).willReturn(Optional.of(session));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(attendanceRepository.existsBySessionIdAndMemberId(session.getId(), member.getId())).willReturn(false);
        given(cohortMemberRepository.findByCohortIdAndMemberId(cohort.getId(), member.getId())).willReturn(Optional.of(cohortMember));
        given(attendanceRepository.save(any(Attendance.class))).willReturn(attendance);
        given(depositHistoryRepository.save(any(DepositHistory.class))).willReturn(depositHistory);

        // when
        AdminAttendanceResponse result = adminRegisterAttendanceUsecase.execute(request);

        // then
        assertThat(result.id()).isEqualTo(attendance.getId());
        assertThat(result.sessionId()).isEqualTo(session.getId());
        assertThat(result.memberId()).isEqualTo(member.getId());
        assertThat(result.status()).isEqualTo(attendance.getAttendanceStatus());
        assertThat(result.lateMinutes()).isEqualTo(attendance.getLateMinutes().getValue());
        assertThat(result.penaltyAmount()).isEqualTo(attendance.getPenaltyAmount().getValue());
    }

    @DisplayName("출결 등록 실패 - 세션 존재하지 않음")
    @Test
    void should_fail_to_register_attendance_when_session_not_found() {
        // given
        AdminRegisterAttendanceRequest request = new AdminRegisterAttendanceRequest(
            session.getId(),
            member.getId(),
            AttendanceStatus.LATE,
            10,
            "지각"
        );
        given(sessionRepository.findById(session.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminRegisterAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.SESSION_NOT_FOUND.getMessage());
    }

    @DisplayName("출결 등록 실패 - 회원 존재하지 않음")
    @Test
    void should_fail_to_register_attendance_when_member_not_found() {
        // given
        AdminRegisterAttendanceRequest request = new AdminRegisterAttendanceRequest(
            session.getId(),
            member.getId(),
            AttendanceStatus.LATE,
            null,
            null
        );
        given(sessionRepository.findById(session.getId())).willReturn(Optional.of(session));
        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminRegisterAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("출결 등록 실패 - 출결 존재")
    @Test
    void should_fail_to_register_attendance_when_attendance_exists() {
        // given
        AdminRegisterAttendanceRequest request = new AdminRegisterAttendanceRequest(
            session.getId(),
            member.getId(),
            AttendanceStatus.PRESENT,
            null,
            null
        );
        given(sessionRepository.findById(session.getId())).willReturn(Optional.of(session));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(attendanceRepository.existsBySessionIdAndMemberId(session.getId(), member.getId())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> adminRegisterAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(AttendanceErrorCode.ATTENDANCE_ALREADY_CHECKED.getMessage());
    }

    @DisplayName("출결 등록 실패 - CohortMember 존재하지 않음")
    @Test
    void should_fail_to_register_attendance_when_cohort_member_not_found() {
        // given
        AdminRegisterAttendanceRequest request = new AdminRegisterAttendanceRequest(
            session.getId(),
            member.getId(),
            AttendanceStatus.LATE,
            null,
            null
        );
        given(sessionRepository.findById(session.getId())).willReturn(Optional.of(session));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(attendanceRepository.existsBySessionIdAndMemberId(session.getId(), member.getId())).willReturn(false);
        given(cohortMemberRepository.findByCohortIdAndMemberId(cohort.getId(), member.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminRegisterAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.COHORT_MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("출결 등록 실패 - 공결 제한 횟수 초과")
    @Test
    void should_fail_to_register_attendance_when_excused_limit_exceeded() {
        // given
        CohortMember cohortMember = CohortMember.builder()
            .id(1L)
            .cohort(cohort)
            .member(member)
            .deposit(new Deposit(100000))
            .excusedCount(new ExcusedCount(3))
            .build();
        AdminRegisterAttendanceRequest request = new AdminRegisterAttendanceRequest(
            session.getId(),
            member.getId(),
            AttendanceStatus.EXCUSED,
            null,
            null
        );
        given(sessionRepository.findById(session.getId())).willReturn(Optional.of(session));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(attendanceRepository.existsBySessionIdAndMemberId(session.getId(), member.getId())).willReturn(false);
        given(cohortMemberRepository.findByCohortIdAndMemberId(cohort.getId(), member.getId())).willReturn(Optional.of(cohortMember));

        // when & then
        assertThatThrownBy(() -> adminRegisterAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.EXCUSE_LIMIT_EXCEEDED.getMessage());
    }
}
