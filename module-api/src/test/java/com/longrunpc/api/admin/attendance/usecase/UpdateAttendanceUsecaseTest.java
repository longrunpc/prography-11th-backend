package com.longrunpc.api.admin.attendance.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.attendance.vo.Reason;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.ExcusedCount;
import com.longrunpc.domain.cohort.entity.DepositType;
import com.longrunpc.domain.cohort.vo.Description;
import com.longrunpc.common.error.AttendanceErrorCode;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.api.admin.attendance.dto.request.UpdateAttendanceRequest;
import com.longrunpc.api.admin.attendance.dto.response.AdminAttendanceResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;

@DisplayName("UpdateAttendanceUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class UpdateAttendanceUsecaseTest {
    @InjectMocks
    private UpdateAttendanceUsecase updateAttendanceUsecase;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private CohortMemberRepository cohortMemberRepository;
    @Mock
    private DepositHistoryRepository depositHistoryRepository;
    
    private Attendance attendance;
    private CohortMember cohortMember;
    private DepositHistory depositHistory;
    private Member member;
    private Cohort cohort;
    private Session session;

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
            .member(member)
            .cohort(cohort)
            .part(null)
            .team(null)
            .deposit(new Deposit(100_000))
            .excusedCount(new ExcusedCount(0))
            .build();
        attendance = Attendance.builder()
            .id(1L)
            .session(session)
            .qrCode(null)
            .member(member)
            .attendanceStatus(AttendanceStatus.LATE)
            .lateMinutes(new LateMinutes(10))
            .penaltyAmount(new PenaltyAmount(5000))
            .reason(new Reason("지각"))
            .checkedInAt(LocalDateTime.now())
            .build();
        depositHistory = DepositHistory.builder()
            .id(1L)
            .cohortMember(cohortMember)
            .attendance(attendance)
            .depositType(DepositType.PENALTY)
            .amount(10000)
            .balanceAfter(100000)
            .description(new Description("test"))
            .build();
    }

    @DisplayName("출결 수정 성공")
    @Test
    void should_update_attendance_when_valid_input() {
        // given
        UpdateAttendanceRequest request = new UpdateAttendanceRequest(AttendanceStatus.ABSENT, null, "test");
        given(attendanceRepository.findById(1L)).willReturn(Optional.of(attendance));
        given(cohortMemberRepository.findByCohortIdAndMemberId(1L, 1L)).willReturn(Optional.of(cohortMember));
        given(depositHistoryRepository.save(any(DepositHistory.class))).willReturn(depositHistory);

        // when
        AdminAttendanceResponse response = updateAttendanceUsecase.execute(request, 1L);

        // then
        assertThat(response.id()).isEqualTo(attendance.getId());
        assertThat(response.status()).isEqualTo(attendance.getAttendanceStatus());
        assertThat(response.lateMinutes()).isNull();
        assertThat(response.penaltyAmount()).isEqualTo(attendance.getPenaltyAmount().getValue());
        assertThat(response.reason()).isEqualTo(attendance.getReason().getValue());
        assertThat(response.checkedInAt()).isNull();
        assertThat(response.createdAt()).isEqualTo(attendance.getCreatedAt());
    }

    @DisplayName("출결 수정 실패 - 출결 존재하지 않음")
    @Test
    void should_fail_update_attendance_when_attendance_not_found() {
        // given
        UpdateAttendanceRequest request = new UpdateAttendanceRequest(AttendanceStatus.ABSENT, null, "test");
        given(attendanceRepository.findById(2L)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> updateAttendanceUsecase.execute(request, 2L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(AttendanceErrorCode.ATTENDANCE_NOT_FOUND.getMessage());
    }   

    @DisplayName("출결 수정 실패 - CohortMember 존재하지 않음")
    @Test
    void should_fail_update_attendance_when_cohort_member_not_found() {
        // given
        UpdateAttendanceRequest request = new UpdateAttendanceRequest(AttendanceStatus.ABSENT, null, "test");
        given(attendanceRepository.findById(1L)).willReturn(Optional.of(attendance));
        given(cohortMemberRepository.findByCohortIdAndMemberId(1L, 1L)).willReturn(Optional.empty());
    
        assertThatThrownBy(() -> updateAttendanceUsecase.execute(request, 1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.COHORT_MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("출결 수정 실패 - 공결 제한 횟수 초과")
    @Test
    void should_fail_update_attendance_when_excused_status_transition_failed() {
        // given
        UpdateAttendanceRequest request = new UpdateAttendanceRequest(AttendanceStatus.EXCUSED, null, "test");
        given(attendanceRepository.findById(1L)).willReturn(Optional.of(attendance));
        given(cohortMemberRepository.findByCohortIdAndMemberId(1L, 1L)).willReturn(Optional.of(cohortMember));
        cohortMember.increaseExcusedCount();
        cohortMember.increaseExcusedCount();
        cohortMember.increaseExcusedCount();

        // when & then
        assertThatThrownBy(() -> updateAttendanceUsecase.execute(request, 1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.EXCUSE_LIMIT_EXCEEDED.getMessage());
    }
    
    @DisplayName("출결 수정 실패 - 보증금 잔액 부족")
    @Test
    void should_fail_update_attendance_when_deposit_is_not_enough() {
        // given
        UpdateAttendanceRequest request = new UpdateAttendanceRequest(AttendanceStatus.ABSENT, null, "test");
        given(attendanceRepository.findById(1L)).willReturn(Optional.of(attendance));
        given(cohortMemberRepository.findByCohortIdAndMemberId(1L, 1L)).willReturn(Optional.of(cohortMember));
        cohortMember.changeDeposit(-100000);

        // when & then
        assertThatThrownBy(() -> updateAttendanceUsecase.execute(request, 1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.DEPOSIT_INSUFFICIENT.getMessage());
    }
}
