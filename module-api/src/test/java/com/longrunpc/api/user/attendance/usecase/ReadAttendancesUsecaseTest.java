package com.longrunpc.api.user.attendance.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.session.vo.QrCodeHashValue;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.attendance.vo.Reason;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.api.user.attendance.dto.response.AttendanceResponse;
import com.longrunpc.domain.attendance.entity.Attendance;

@DisplayName("ReadAttendancesUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class ReadAttendancesUsecaseTest {
    @InjectMocks
    private ReadAttendancesUsecase readAttendancesUsecase;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private Cohort cohort;
    private Session session;
    private Attendance attendance;
    private QrCode qrCode;
    
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
            .sessionStatus(SessionStatus.IN_PROGRESS)
            .build();
        qrCode = QrCode.builder()
            .id(1L)
            .session(session)
            .hashValue(new QrCodeHashValue("hashValue"))
            .expiresAt(LocalDateTime.now().plusHours(1))
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
    }

    @DisplayName("출결 목록 조회 성공")
    @Test
    void should_read_attendances_when_valid_input() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(attendanceRepository.findAllWithSessionByMemberId(1L)).willReturn(List.of(attendance));

        // when
        List<AttendanceResponse> result = readAttendancesUsecase.execute(1L);

        // then
        assertThat(result).isEqualTo(List.of(AttendanceResponse.of(attendance)));
    }

    @DisplayName("출결 목록 조회 실패 - 회원 존재하지 않음")
    @Test
    void should_fail_to_read_attendances_when_member_not_found() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readAttendancesUsecase.execute(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }
}
