package com.longrunpc.domain.cohort.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.cohort.vo.Description;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.ExcusedCount;
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

@DisplayName("DepositHistory 엔티티 테스트")
public class DepositHistoryTest {

    private CohortMember cohortMember;
    private Attendance attendance;

    @BeforeEach
    void setUp() {
        // given
        Cohort cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(11))
            .cohortName(new CohortName("11기"))
            .build();

        Member member = Member.builder()
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
            .deposit(new Deposit(100_000))
            .excusedCount(new ExcusedCount(0))
            .build();

        Session session = Session.builder()
            .id(1L)
            .cohort(cohort)
            .title(new SessionTitle("test"))
            .sessionDate(LocalDate.now().plusDays(1))
            .sessionTime(LocalTime.now().plusHours(1))
            .sessionLocation(new SessionLocation("강남역"))
            .sessionStatus(SessionStatus.SCHEDULED)
            .build();

        QrCode qrCode = QrCode.createQrCode(session);
        attendance = Attendance.createAttendance(session, qrCode, cohortMember, new LateMinutes(5));
    }

    @DisplayName("builder 테스트")
    @Nested
    class BuilderTest {

        @DisplayName("유효한 입력 시 정상 생성")
        @Test
        void should_create_deposit_history_when_valid_input() {
            // given
            Description description = new Description("지각 벌금 차감");

            // when
            DepositHistory depositHistory = DepositHistory.builder()
                .id(1L)
                .cohortMember(cohortMember)
                .attendance(attendance)
                .depositType(DepositType.PENALTY)
                .amount(5_000)
                .balanceAfter(95_000)
                .description(description)
                .build();

            // then
            assertThat(depositHistory).extracting(
                "cohortMember",
                "attendance",
                "depositType",
                "amount",
                "balanceAfter",
                "description"
            ).containsExactly(
                cohortMember,
                attendance,
                DepositType.PENALTY,
                5_000,
                95_000,
                description
            );
            assertThat(depositHistory.getId()).isEqualTo(1L);
        }
    }
}
