package com.longrunpc.domain.attendance.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.longrunpc.common.exception.BusinessException;
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
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;

@DisplayName("Attendance 엔티티 테스트")
public class AttendanceTest {

    private Session session;
    private QrCode qrCode;
    private Member member;

    @BeforeEach
    void setUp() {
        // given
        Cohort cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(11))
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

        session = Session.builder()
            .id(1L)
            .cohort(cohort)
            .title(new SessionTitle("test"))
            .sessionDate(LocalDate.now().plusDays(1))
            .sessionTime(LocalTime.now().plusHours(1))
            .sessionLocation(new SessionLocation("강남역"))
            .sessionStatus(SessionStatus.SCHEDULED)
            .build();

        qrCode = QrCode.createQrCode(session);
    }

    @DisplayName("createAttendance 메서드 테스트")
    @Nested
    class CreateAttendanceTest {

        @DisplayName("지각 0분일 때 출석 상태 PRESENT 로 생성")
        @Test
        void should_create_present_attendance_when_late_minutes_is_zero() {
            // given
            LateMinutes lateMinutes = new LateMinutes(0);

            // when
            Attendance attendance = Attendance.createAttendance(session, qrCode, member, AttendanceStatus.PRESENT, lateMinutes, new PenaltyAmount(0));

            // then
            assertThat(attendance.getSession()).isEqualTo(session);
            assertThat(attendance.getQrCode()).isEqualTo(qrCode);
            assertThat(attendance.getMember()).isEqualTo(member);
            assertThat(attendance.getAttendanceStatus()).isEqualTo(AttendanceStatus.PRESENT);
            assertThat(attendance.getLateMinutes()).isEqualTo(new LateMinutes(0));
            assertThat(attendance.getPenaltyAmount()).isEqualTo(new PenaltyAmount(0));
            assertThat(attendance.getCheckedInAt()).isAfter(LocalDateTime.now().minusSeconds(1));
        }
    }

    @DisplayName("calculatePenaltyAmount 메서드 테스트")
    @Nested
    class CalculatePenaltyAmountTest {

        @DisplayName("LATE 상태는 분당 벌금을 계산")
        @Test
        void should_calculate_late_penalty_amount() {
            // when
            PenaltyAmount penaltyAmount = Attendance.calculatePenaltyAmount(AttendanceStatus.LATE, new LateMinutes(3));

            // then
            assertThat(penaltyAmount).isEqualTo(new PenaltyAmount(3_000));
        }

        @DisplayName("LATE 상태 벌금은 최대값을 초과하지 않음")
        @Test
        void should_limit_late_penalty_to_maximum() {
            // when
            PenaltyAmount penaltyAmount = Attendance.calculatePenaltyAmount(AttendanceStatus.LATE, new LateMinutes(30));

            // then
            assertThat(penaltyAmount).isEqualTo(new PenaltyAmount(10_000));
        }

        @DisplayName("ABSENT 상태는 최대 벌금 부과")
        @Test
        void should_return_max_penalty_when_absent() {
            // when
            PenaltyAmount penaltyAmount = Attendance.calculatePenaltyAmount(AttendanceStatus.ABSENT, new LateMinutes(0));

            // then
            assertThat(penaltyAmount).isEqualTo(new PenaltyAmount(10_000));
        }

        @DisplayName("EXCUSED 상태는 벌금 0")
        @Test
        void should_return_zero_penalty_when_excused() {
            // when
            PenaltyAmount penaltyAmount = Attendance.calculatePenaltyAmount(AttendanceStatus.EXCUSED, new LateMinutes(0));

            // then
            assertThat(penaltyAmount).isEqualTo(new PenaltyAmount(0));
        }
    }

    @DisplayName("changeAttendanceStatus 메서드 테스트")
    @Nested
    class ChangeAttendanceStatusTest {

        @DisplayName("유효한 상태로 변경")
        @Test
        void should_change_attendance_status_when_valid_input() {
            // given
            Attendance attendance = Attendance.createAttendance(session, qrCode, member, AttendanceStatus.PRESENT, new LateMinutes(0), new PenaltyAmount(0));

            // when
            attendance.changeAttendanceStatus(AttendanceStatus.ABSENT);

            // then
            assertThat(attendance.getAttendanceStatus()).isEqualTo(AttendanceStatus.ABSENT);
        }

        @DisplayName("attendanceStatus 필드 null 시 예외 발생")
        @Test
        void should_throw_exception_when_attendance_status_is_null() {
            // given
            Attendance attendance = Attendance.createAttendance(session, qrCode, member, AttendanceStatus.PRESENT, new LateMinutes(0), new PenaltyAmount(0));

            // when & then
            assertThatThrownBy(() -> attendance.changeAttendanceStatus(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("changeLateMinutes 메서드 테스트")
    @Nested
    class ChangeLateMinutesTest {

        @DisplayName("유효한 입력 시 정상 변경")
        @Test
        void should_change_late_minutes_when_valid_input() {
            // given
            Attendance attendance = Attendance.createAttendance(session, qrCode, member, AttendanceStatus.PRESENT, new LateMinutes(0), new PenaltyAmount(0));

            // when
            attendance.changeLateMinutes(new LateMinutes(10));

            // then
            assertThat(attendance.getLateMinutes()).isEqualTo(new LateMinutes(10));
        }
    }

    @DisplayName("changePenaltyAmount 메서드 테스트")
    @Nested
    class ChangePenaltyAmountTest {

        @DisplayName("유효한 입력 시 정상 변경")
        @Test
        void should_change_penalty_amount_when_valid_input() {
            // given
            Attendance attendance = Attendance.createAttendance(session, qrCode, member, AttendanceStatus.PRESENT, new LateMinutes(0), new PenaltyAmount(0));
            PenaltyAmount penaltyAmount = new PenaltyAmount(3_000);

            // when
            attendance.changePenaltyAmount(penaltyAmount);

            // then
            assertThat(attendance.getPenaltyAmount()).isEqualTo(penaltyAmount);
        }

        @DisplayName("penaltyAmount 필드 null 시 예외 발생")
        @Test
        void should_throw_exception_when_penalty_amount_is_null() {
            // given
            Attendance attendance = Attendance.createAttendance(session, qrCode, member, AttendanceStatus.PRESENT, new LateMinutes(0), new PenaltyAmount(0));

            // when & then
            assertThatThrownBy(() -> attendance.changePenaltyAmount(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("changeReason 메서드 테스트")
    @Nested
    class ChangeReasonTest {

        @DisplayName("유효한 입력 시 정상 변경")
        @Test
        void should_change_reason_when_valid_input() {
            // given
            Attendance attendance = Attendance.createAttendance(session, qrCode, member, AttendanceStatus.PRESENT, new LateMinutes(0), new PenaltyAmount(0));
            Reason reason = new Reason("지각 사유");

            // when
            attendance.changeReason(reason);

            // then
            assertThat(attendance.getReason()).isEqualTo(reason);
        }

        @DisplayName("null 입력 시 reason 필드 null 저장")
        @Test
        void should_set_reason_to_null_when_null_input() {
            // given
            Attendance attendance = Attendance.createAttendance(session, qrCode, member, AttendanceStatus.PRESENT, new LateMinutes(0), new PenaltyAmount(0));
            attendance.changeReason(new Reason("사유"));

            // when
            attendance.changeReason(null);

            // then
            assertThat(attendance.getReason()).isNull();
        }
    }

    @DisplayName("changeLateMinutes 메서드 음수 입력 테스트")
    @Nested
    class ChangeLateMinutesNegativeTest {

        @DisplayName("음수 지각분 입력 시 예외 발생")
        @Test
        void should_throw_exception_when_negative_late_minutes_input() {
            // given
            Attendance attendance = Attendance.createAttendance(session, qrCode, member, AttendanceStatus.PRESENT, new LateMinutes(0), new PenaltyAmount(0));

            // when & then
            assertThatThrownBy(() -> attendance.changeLateMinutes(new LateMinutes(-1)))
                .isInstanceOf(BusinessException.class);
        }
    }
}
