package com.longrunpc.api.admin.attendance.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.api.admin.attendance.dto.response.SessionAttendanceResponse;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.attendance.vo.Reason;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;

@DisplayName("ReadSessionAttendanceUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class ReadSessionAttendanceUsecaseTest {
    @InjectMocks
    private ReadSessionAttendanceUsecase readSessionAttendanceUsecase;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private SessionRepository sessionRepository;

    private Session session;
    private Cohort cohort;
    private Member member;
    private List<Attendance> attendances;

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
        session = Session.builder()
            .id(1L)
            .cohort(cohort)
            .title(new SessionTitle("test"))
            .sessionDate(LocalDate.now().plusDays(1))
            .sessionTime(LocalTime.now().plusHours(1))
            .sessionLocation(new SessionLocation("강남역"))
            .sessionStatus(SessionStatus.SCHEDULED)
            .build();
        attendances = List.of(
            Attendance.builder()
                .id(1L)
                .session(session)
                .qrCode(null)
                .member(member)
                .attendanceStatus(AttendanceStatus.PRESENT)
                .lateMinutes(new LateMinutes(5))
                .penaltyAmount(new PenaltyAmount(10000))
                .reason(new Reason("test"))
                .checkedInAt(LocalDateTime.now())
                .build()
        );
    }

    @DisplayName("세션 출결 조회 성공")
    @Test
    void should_get_session_attendance_when_valid_input() {
        // given
        given(sessionRepository.findById(1L)).willReturn(Optional.of(session));
        given(attendanceRepository.findBySessionId(1L)).willReturn(attendances);

        // when
        SessionAttendanceResponse result = readSessionAttendanceUsecase.execute(1L);

        // then
        assertThat(result).isEqualTo(SessionAttendanceResponse.of(session, attendances));
    }

    @DisplayName("세션 출결 조회 실패 - 세션 존재하지 않음")
    @Test
    void should_fail_to_get_session_attendance_when_session_not_found() {
        // given
        given(sessionRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readSessionAttendanceUsecase.execute(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.SESSION_NOT_FOUND.getMessage());
    }
}
