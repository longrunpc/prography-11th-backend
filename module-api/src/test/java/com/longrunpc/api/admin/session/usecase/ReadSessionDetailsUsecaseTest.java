package com.longrunpc.api.admin.session.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import com.longrunpc.api.admin.session.dto.request.ReadSessionDetailsRequest;
import com.longrunpc.api.admin.session.dto.response.SessionDetailResponse;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.ExcusedCount;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.session.vo.QrCodeHashValue;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.attendance.vo.Reason;

@DisplayName("ReadSessionDetailsUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class ReadSessionDetailsUsecaseTest {
    @InjectMocks
    private ReadSessionDetailsUsecase readSessionDetailsUsecase;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private CohortRepository cohortRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private QrCodeRepository qrCodeRepository;

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    private Cohort cohort;
    private Member member;
    private CohortMember cohortMember;
    private List<Session> sessions;
    private List<Attendance> attendances;
    private List<QrCode> qrCodes;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(readSessionDetailsUsecase, "currentGeneration", currentGeneration);
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(currentGeneration))
            .cohortName(new CohortName("11기"))
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
        sessions = List.of(
            Session.builder()
                .id(1L)
                .cohort(cohort)
                .title(new SessionTitle("test"))
                .sessionDate(LocalDate.now().plusDays(1))
                .sessionTime(LocalTime.now().plusHours(1))
                .sessionLocation(new SessionLocation("강남역"))
                .sessionStatus(SessionStatus.SCHEDULED)
                .build()
        );
        qrCodes = List.of(
            QrCode.builder()
                .id(1L)
                .session(sessions.get(0))
                .hashValue(new QrCodeHashValue("hashValue"))
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build()
        );
        attendances = List.of(
            Attendance.builder()
                .id(1L)
                .session(sessions.get(0))
                .qrCode(qrCodes.get(0))
                .cohortMember(cohortMember)
                .attendanceStatus(AttendanceStatus.PRESENT)
                .lateMinutes(new LateMinutes(5))
                .penaltyAmount(new PenaltyAmount(10000))
                .reason(new Reason("test"))
                .checkedInAt(LocalDateTime.now())
                .build()
        );
    }

    @DisplayName("세션 상세 조회 성공")
    @Test
    void should_read_session_details_when_valid_input() {
        // given
        LocalDate now = LocalDate.now();
        LocalDate dateFrom = now.minusDays(1);
        LocalDate dateTo = now.plusDays(1);
        given(cohortRepository.findByGeneration(new Generation(currentGeneration))).willReturn(Optional.of(cohort));
        given(sessionRepository.findByCohortIdAndSessionDateBetweenAndSessionStatus(cohort.getId(), dateFrom, dateTo, SessionStatus.SCHEDULED)).willReturn(sessions);
        given(attendanceRepository.findBySessionIdIn(sessions.stream().map(Session::getId).toList())).willReturn(attendances);
        given(qrCodeRepository.findBySessionIdInAndExpiresAtAfter(
            eq(sessions.stream().map(Session::getId).toList()),
            any(LocalDateTime.class)
        )).willReturn(qrCodes);
        
        // when
        List<SessionDetailResponse> result = readSessionDetailsUsecase.execute(new ReadSessionDetailsRequest(dateFrom, dateTo, SessionStatus.SCHEDULED));

        // then
        assertThat(result).hasSize(sessions.size());
        assertThat(result).isEqualTo(sessions.stream().map(session -> SessionDetailResponse.of(session, attendances, qrCodes.stream().anyMatch(qrCode -> qrCode.getSession().getId() == session.getId()))).toList());
    }
}
