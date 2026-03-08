package com.longrunpc.api.user.attendance.usecase;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.cohort.entity.DepositType;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.attendance.vo.Reason;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Description;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.domain.session.vo.QrCodeHashValue;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.ExcusedCount;
import com.longrunpc.api.user.attendance.dto.request.RegisterAttendanceRequest;
import com.longrunpc.api.user.attendance.dto.response.AttendanceDetailResponse;
import com.longrunpc.common.error.AttendanceErrorCode;
import com.longrunpc.common.error.CohortErrorCode;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@DisplayName("RegisterAttendanceUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class RegisterAttendanceUsecaseTest {
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private QrCodeRepository qrCodeRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private CohortMemberRepository cohortMemberRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CohortRepository cohortRepository;
    @Mock
    private DepositHistoryRepository depositHistoryRepository;

    @InjectMocks
    private RegisterAttendanceUsecase registerAttendanceUsecase;

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    private QrCode qrCode;
    private Session session;
    private Cohort cohort;
    private Member member;
    private CohortMember cohortMember;
    private Attendance attendance;
    private DepositHistory depositHistory;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(registerAttendanceUsecase, "currentGeneration", currentGeneration);
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
            .sessionStatus(SessionStatus.IN_PROGRESS)
            .build();
        qrCode = QrCode.builder()
            .id(1L)
            .session(session)
            .hashValue(new QrCodeHashValue("hashValue"))
            .expiresAt(LocalDateTime.now().plusHours(1))
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
            .qrCode(qrCode)
            .member(member)
            .attendanceStatus(AttendanceStatus.PRESENT)
            .lateMinutes(new LateMinutes(5))
            .penaltyAmount(new PenaltyAmount(10000))
            .reason(new Reason("test"))
            .checkedInAt(LocalDateTime.now())
            .build();
        depositHistory = DepositHistory.builder()
            .id(1L)
            .cohortMember(cohortMember)
            .attendance(attendance)
            .depositType(DepositType.PENALTY)
            .amount(10000)
            .balanceAfter(10000)
            .description(new Description("test"))
            .build();
    }

    @DisplayName("출결 등록 성공")
    @Test
    void should_register_attendance_when_valid_input() {
        // given
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            "hashValue",
            1L
        );

        given(qrCodeRepository.findByHashValue(request.hashValue())).willReturn(Optional.of(qrCode));
        given(sessionRepository.findById(qrCode.getSession().getId())).willReturn(Optional.of(session));
        given(cohortRepository.findByGeneration(new Generation(currentGeneration))).willReturn(Optional.of(cohort));
        given(memberRepository.findById(request.memberId())).willReturn(Optional.of(member));
        given(cohortMemberRepository.findByCohortIdAndMemberId(cohort.getId(), member.getId())).willReturn(Optional.of(cohortMember));
        given(attendanceRepository.existsBySessionIdAndMemberId(session.getId(), member.getId())).willReturn(false);
        given(attendanceRepository.save(any(Attendance.class))).willReturn(attendance);

        // when
        AttendanceDetailResponse result = registerAttendanceUsecase.execute(request);

        // then
        assertThat(result.id()).isEqualTo(attendance.getId());
        assertThat(result.sessionId()).isEqualTo(session.getId());
        assertThat(result.memberId()).isEqualTo(member.getId());
        assertThat(result.status()).isEqualTo(attendance.getAttendanceStatus());
        assertThat(result.lateMinutes()).isEqualTo(attendance.getLateMinutes().getValue());
        assertThat(result.penaltyAmount()).isEqualTo(attendance.getPenaltyAmount().getValue());
        assertThat(result.reason()).isEqualTo(attendance.getReason().getValue());
        assertThat(result.checkedInAt()).isEqualTo(attendance.getCheckedInAt());
    }

    @DisplayName("출결 등록 실패 - QR 코드 없음")
    @Test
    void should_fail_to_register_attendance_when_qr_code_not_found() {
        // given
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            "hashValue",
            1L
        );
        given(qrCodeRepository.findByHashValue(request.hashValue())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> registerAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.QR_INVALID.getMessage());
    }

    @DisplayName("출결 등록 실패 - QR 코드 만료")
    @Test
    void should_fail_to_register_attendance_when_qr_code_expired() {
        // given
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            "hashValue",
            1L
        );
        given(qrCodeRepository.findByHashValue(request.hashValue())).willReturn(Optional.of(qrCode));
        qrCode.expire();
        
        // when & then
        assertThatThrownBy(() -> registerAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.QR_EXPIRED.getMessage());
    }

    @DisplayName("출결 등록 실패 - 일정 없음")
    @Test
    void should_fail_to_register_attendance_when_session_not_found() {
        // given
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            "hashValue",
            1L
        );
        given(qrCodeRepository.findByHashValue(request.hashValue())).willReturn(Optional.of(qrCode));
        given(sessionRepository.findById(qrCode.getSession().getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> registerAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.SESSION_NOT_FOUND.getMessage());
    }

    @DisplayName("출결 등록 실패 - 일정 진행 중이 아님")
    @Test
    void should_fail_to_register_attendance_when_session_not_in_progress() {
        // given
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            "hashValue",
            1L
        );
        given(qrCodeRepository.findByHashValue(request.hashValue())).willReturn(Optional.of(qrCode));
        given(sessionRepository.findById(qrCode.getSession().getId())).willReturn(Optional.of(session));
        session.changeSessionStatus(SessionStatus.COMPLETED);

        // when & then
        assertThatThrownBy(() -> registerAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.SESSION_NOT_IN_PROGRESS.getMessage());
    }

    @DisplayName("출결 등록 실패 - 회원 없음")
    @Test
    void should_fail_to_register_attendance_when_member_not_found() {
        // given
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            "hashValue",
            1L
        );

        given(qrCodeRepository.findByHashValue(request.hashValue())).willReturn(Optional.of(qrCode));
        given(sessionRepository.findById(qrCode.getSession().getId())).willReturn(Optional.of(session));
        given(memberRepository.findById(request.memberId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> registerAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("출결 등록 실패 - 회원 탈퇴 상태")
    @Test
    void should_fail_to_register_attendance_when_member_withdrawn() {
        // given
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            "hashValue",
            1L
        );

        given(qrCodeRepository.findByHashValue(request.hashValue())).willReturn(Optional.of(qrCode));
        given(sessionRepository.findById(qrCode.getSession().getId())).willReturn(Optional.of(session));
        given(memberRepository.findById(request.memberId())).willReturn(Optional.of(member));
        member.withdraw();

        // when & then
        assertThatThrownBy(() -> registerAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.MEMBER_WITHDRAWN.getMessage());
    }

    @DisplayName("출결 등록 실패 - 중복 출결")
    @Test
    void should_fail_to_register_attendance_when_duplicate_attendance() {
        // given
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            "hashValue",
            1L
        );

        given(qrCodeRepository.findByHashValue(request.hashValue())).willReturn(Optional.of(qrCode));
        given(sessionRepository.findById(qrCode.getSession().getId())).willReturn(Optional.of(session));
        given(memberRepository.findById(request.memberId())).willReturn(Optional.of(member));
        given(attendanceRepository.existsBySessionIdAndMemberId(session.getId(), member.getId())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> registerAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(AttendanceErrorCode.ATTENDANCE_ALREADY_CHECKED.getMessage());
    }

    @DisplayName("출결 등록 실패 - 현재 기수의 맴버 존재하지 않음")
    @Test
    void should_fail_to_register_attendance_when_member_not_in_current_cohort() {
        // given
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            "hashValue",
            1L
        );

        given(qrCodeRepository.findByHashValue(request.hashValue())).willReturn(Optional.of(qrCode));
        given(sessionRepository.findById(qrCode.getSession().getId())).willReturn(Optional.of(session));
        given(memberRepository.findById(request.memberId())).willReturn(Optional.of(member));
        given(cohortRepository.findByGeneration(new Generation(currentGeneration))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> registerAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.COHORT_NOT_FOUND.getMessage());
    }

    @DisplayName("출결 등록 실패 - 기수 회원 정보 존재하지 않음")
    @Test
    void should_fail_to_register_attendance_when_cohort_member_not_found() {
        // given
        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            "hashValue",
            1L
        );

        given(qrCodeRepository.findByHashValue(request.hashValue())).willReturn(Optional.of(qrCode));
        given(sessionRepository.findById(qrCode.getSession().getId())).willReturn(Optional.of(session));
        given(memberRepository.findById(request.memberId())).willReturn(Optional.of(member));
        given(cohortRepository.findByGeneration(new Generation(currentGeneration))).willReturn(Optional.of(cohort));
        given(cohortMemberRepository.findByCohortIdAndMemberId(cohort.getId(), member.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> registerAttendanceUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.COHORT_MEMBER_NOT_FOUND.getMessage());
    }
}
