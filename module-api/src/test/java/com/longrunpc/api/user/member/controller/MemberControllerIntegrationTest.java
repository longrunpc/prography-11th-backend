package com.longrunpc.api.user.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.AttendanceApplication;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.attendance.vo.Reason;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;

@SpringBootTest(classes = AttendanceApplication.class)
@AutoConfigureMockMvc
@Transactional
class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private CohortMemberRepository cohortMemberRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @DisplayName("회원 조회 성공")
    @Test
    void should_read_member() throws Exception {
        // given
        Member member = Objects.requireNonNull(memberRepository.save(Member.createMember(
            new LoginId("member-read@example.com"),
            new Password("password"),
            new MemberName("조회회원"),
            new Phone("01022223333")
        )));

        // when & then
        mockMvc.perform(get("/members/{id}", member.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(member.getId()))
            .andExpect(jsonPath("$.data.loginId").value("member-read@example.com"))
            .andExpect(jsonPath("$.data.name").value("조회회원"))
            .andExpect(jsonPath("$.data.phone").value("01022223333"))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("회원 조회 실패 - 존재하지 않는 회원")
    @Test
    void should_fail_when_member_not_found() throws Exception {
        // when & then
        mockMvc.perform(get("/members/{id}", 999999L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(MemberErrorCode.MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()));
    }

    @DisplayName("회원 조회 실패 - 잘못된 id 타입")
    @Test
    void should_fail_when_member_id_is_invalid_type() throws Exception {
        // when & then
        mockMvc.perform(get("/members/{id}", "invalid-id"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(GlobalErrorCode.INTERNAL_ERROR.getCode()))
            .andExpect(jsonPath("$.error.message").value(GlobalErrorCode.INTERNAL_ERROR.getMessage()));
    }

    @DisplayName("내 출결 요약 조회 성공")
    @Test
    void should_read_member_attendance_summary() throws Exception {
        // given
        Cohort cohort = findCurrentCohort();
        Member member = createMember("summary@example.com", "요약회원", "01055554444");
        createCohortMember(member, cohort);
        Session presentSession = createSession(cohort, "요약세션-출석");
        Session lateSession = createSession(cohort, "요약세션-지각");

        createAttendance(presentSession, member, AttendanceStatus.PRESENT, 0, 0);
        createAttendance(lateSession, member, AttendanceStatus.LATE, 5, 5000);

        // when & then
        mockMvc.perform(get("/members/{memberId}/attendance-summary", member.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.memberId").value(member.getId()))
            .andExpect(jsonPath("$.data.present").value(1))
            .andExpect(jsonPath("$.data.late").value(1))
            .andExpect(jsonPath("$.data.totalPenalty").value(5000))
            .andExpect(jsonPath("$.data.deposit").isNumber())
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("내 출결 요약 조회 실패 - 기수 회원 정보 없음")
    @Test
    void should_fail_read_attendance_summary_when_cohort_member_not_found() throws Exception {
        // given
        Member member = createMember("summary-no-cohort-member@example.com", "기수없음", "01066667777");

        // when & then
        mockMvc.perform(get("/members/{memberId}/attendance-summary", member.getId()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(CohortErrorCode.COHORT_MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(CohortErrorCode.COHORT_MEMBER_NOT_FOUND.getMessage()));
    }

    @DisplayName("내 출결 요약 조회 실패 - 잘못된 memberId 타입")
    @Test
    void should_fail_read_attendance_summary_when_member_id_is_invalid_type() throws Exception {
        // when & then
        mockMvc.perform(get("/members/{memberId}/attendance-summary", "invalid-id"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(GlobalErrorCode.INTERNAL_ERROR.getCode()))
            .andExpect(jsonPath("$.error.message").value(GlobalErrorCode.INTERNAL_ERROR.getMessage()));
    }

    private Cohort findCurrentCohort() {
        return cohortRepository.findByGeneration(new Generation(11))
            .orElseThrow(() -> new IllegalStateException("cohort 11 should exist"));
    }

    private Member createMember(String loginId, String name, String phone) {
        return Objects.requireNonNull(memberRepository.save(Member.createMember(
            new LoginId(loginId),
            new Password("password"),
            new MemberName(name),
            new Phone(phone)
        )));
    }

    private CohortMember createCohortMember(Member member, Cohort cohort) {
        return Objects.requireNonNull(cohortMemberRepository.save(CohortMember.createCohortMember(member, cohort, null, null)));
    }

    private Session createSession(Cohort cohort, String title) {
        Session session = Session.builder()
            .cohort(cohort)
            .title(new SessionTitle(title))
            .sessionDate(LocalDate.now())
            .sessionTime(LocalTime.now().minusMinutes(5))
            .sessionLocation(new SessionLocation("강남"))
            .sessionStatus(SessionStatus.IN_PROGRESS)
            .build();
        return Objects.requireNonNull(sessionRepository.save(session));
    }

    private Attendance createAttendance(Session session, Member member, AttendanceStatus status, Integer lateMinutes, int penalty) {
        Attendance attendance = Attendance.builder()
            .session(session)
            .qrCode(null)
            .member(member)
            .attendanceStatus(status)
            .lateMinutes(lateMinutes == null ? null : new LateMinutes(lateMinutes))
            .penaltyAmount(new PenaltyAmount(penalty))
            .reason(new Reason("요약테스트"))
            .checkedInAt(LocalDateTime.now())
            .build();
        return Objects.requireNonNull(attendanceRepository.save(attendance));
    }
}
