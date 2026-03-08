package com.longrunpc.api.user.member.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.attendance.vo.Reason;
import com.longrunpc.api.user.member.dto.response.MemberAttendanceSummaryResponse;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.ExcusedCount;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReadMemberAttendanceSummaryUsecase 테스트")
public class ReadMemberAttendanceSummaryUsecaseTest {
    @InjectMocks
    private ReadMemberAttendanceSummaryUsecase readMemberAttendanceSummaryUsecase;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock   
    private CohortMemberRepository cohortMemberRepository;
    @Mock
    private CohortRepository cohortRepository;
    @Mock
    private MemberRepository memberRepository;

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    private Cohort cohort;
    private Member member;
    private Session session;
    private CohortMember cohortMember;
    private List<Attendance> attendances;   
    private Map<AttendanceStatus, Long> attendanceStatusMap;
    private int totalPenalty;
    
    @BeforeEach
    void setUp() {
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
        attendances = List.of(
            Attendance.builder()
                .id(1L)
                .member(member)
                .session(session)
                .qrCode(null)
                .attendanceStatus(AttendanceStatus.PRESENT)
                .lateMinutes(null)
                .penaltyAmount(new PenaltyAmount(0))
                .reason(new Reason("test"))
                .checkedInAt(LocalDateTime.now())
                .build(),
            Attendance.builder()
                .id(2L)
                .member(member)
                .session(session)
                .qrCode(null)
                .lateMinutes(null)
                .penaltyAmount(new PenaltyAmount(0))
                .reason(new Reason("test"))
                .checkedInAt(LocalDateTime.now())
                .attendanceStatus(AttendanceStatus.ABSENT)
                .build(),
            Attendance.builder()
                .id(3L)
                .member(member)
                .session(session)
                .qrCode(null)
                .lateMinutes(new LateMinutes(5))
                .penaltyAmount(new PenaltyAmount(10000))
                .reason(new Reason("test"))
                .checkedInAt(LocalDateTime.now())
                .attendanceStatus(AttendanceStatus.LATE)
                .build(),
            Attendance.builder()
                .id(4L)
                .member(member)
                .session(session)
                .qrCode(null)
                .lateMinutes(null)
                .penaltyAmount(new PenaltyAmount(0))
                .reason(new Reason("test"))
                .checkedInAt(LocalDateTime.now())
                .attendanceStatus(AttendanceStatus.EXCUSED)
                .build(),
            Attendance.builder()
                .id(5L)
                .member(member)
                .session(session)
                .qrCode(null)
                .lateMinutes(null)
                .penaltyAmount(new PenaltyAmount(0))
                .reason(new Reason("test"))
                .checkedInAt(LocalDateTime.now())
                .attendanceStatus(AttendanceStatus.PRESENT)
                .build()
        );
        totalPenalty = 10000;
    }

    @DisplayName("출결 요약 조회 성공")
    @Test
    void should_get_attendance_summary_when_valid_input() {
        // given
        given(cohortRepository.findByGeneration(new Generation(currentGeneration))).willReturn(Optional.of(cohort));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(cohortMemberRepository.findByCohortIdAndMemberId(cohort.getId(), member.getId())).willReturn(Optional.of(cohortMember));
        given(attendanceRepository.findAllByMemberId(member.getId())).willReturn(attendances);
        
        // when
        MemberAttendanceSummaryResponse result = readMemberAttendanceSummaryUsecase.execute(1L);

        // then
        assertThat(result.memberId()).isEqualTo(member.getId());
        assertThat(result.present()).isEqualTo(2);
        assertThat(result.absent()).isEqualTo(1);
        assertThat(result.late()).isEqualTo(1);
        assertThat(result.excused()).isEqualTo(1);
        assertThat(result.totalPenalty()).isEqualTo(totalPenalty);
        assertThat(result.deposit()).isEqualTo(cohortMember.getDeposit().getValue());
    }

    @DisplayName("출결 요약 조회 실패 - 회원 존재하지 않음")
    @Test
    void should_fail_to_get_attendance_summary_when_member_not_found() {
        // given
        given(cohortRepository.findByGeneration(new Generation(currentGeneration))).willReturn(Optional.of(cohort));
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readMemberAttendanceSummaryUsecase.execute(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }
}
