package com.longrunpc.api.admin.attendance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longrunpc.api.AttendanceApplication;
import com.longrunpc.api.admin.attendance.dto.request.AdminRegisterAttendanceRequest;
import com.longrunpc.api.admin.attendance.dto.request.UpdateAttendanceRequest;
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
class AdminAttendanceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private CohortMemberRepository cohortMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("관리자 출결 등록 성공")
    @Test
    void should_register_attendance_by_admin() throws Exception {
        Cohort cohort = findCurrentCohort();
        Member member = createMember("admin-attendance-register@example.com", "관리자출결등록", "01011112222");
        createCohortMember(member, cohort);
        Session session = createSession(cohort, "관리자출결등록세션");

        AdminRegisterAttendanceRequest request = new AdminRegisterAttendanceRequest(
            session.getId(),
            member.getId(),
            AttendanceStatus.LATE,
            7,
            null
        );

        mockMvc.perform(post("/admin/attendances")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.sessionId").value(session.getId()))
            .andExpect(jsonPath("$.data.memberId").value(member.getId()))
            .andExpect(jsonPath("$.data.status").value("LATE"))
            .andExpect(jsonPath("$.data.lateMinutes").value(7))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("관리자 출결 수정 성공")
    @Test
    void should_update_attendance_by_admin() throws Exception {
        Cohort cohort = findCurrentCohort();
        Member member = createMember("admin-attendance-update@example.com", "관리자출결수정", "01022223333");
        createCohortMember(member, cohort);
        Session session = createSession(cohort, "관리자출결수정세션");
        Attendance attendance = createAttendance(session, member, AttendanceStatus.LATE, 10, 10000, "초기 사유");

        UpdateAttendanceRequest request = new UpdateAttendanceRequest(
            AttendanceStatus.ABSENT,
            null,
            "수정 사유"
        );

        mockMvc.perform(put("/admin/attendances/{id}", attendance.getId())
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(attendance.getId()))
            .andExpect(jsonPath("$.data.status").value("ABSENT"))
            .andExpect(jsonPath("$.data.lateMinutes").isEmpty())
            .andExpect(jsonPath("$.data.reason").value("수정 사유"))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("일정별 출결 요약 조회 성공")
    @Test
    void should_read_session_attendance_summary() throws Exception {
        Cohort cohort = findCurrentCohort();
        Session session = createSession(cohort, "출결요약세션");
        Member presentMember = createMember("admin-summary-present@example.com", "요약출석", "01033334444");
        Member lateMember = createMember("admin-summary-late@example.com", "요약지각", "01044445555");

        createCohortMember(presentMember, cohort);
        createCohortMember(lateMember, cohort);
        createAttendance(session, presentMember, AttendanceStatus.PRESENT, 0, 0, "정상 출석");
        createAttendance(session, lateMember, AttendanceStatus.LATE, 5, 5000, "지각");

        mockMvc.perform(get("/admin/attendances/sessions/{sessionId}/summary", session.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[1].memberId").value(presentMember.getId()))
            .andExpect(jsonPath("$.data[2].memberId").value(lateMember.getId()))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("회원 출결 상세 조회 성공")
    @Test
    void should_read_member_attendance_detail() throws Exception {
        Cohort cohort = findCurrentCohort();
        Session session = createSession(cohort, "회원상세조회세션");
        Member member = createMember("admin-member-detail@example.com", "회원상세", "01055556666");
        createCohortMember(member, cohort);
        createAttendance(session, member, AttendanceStatus.PRESENT, 0, 0, "정상 출석");

        mockMvc.perform(get("/admin/attendances/members/{memberId}", member.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.memberId").value(member.getId()))
            .andExpect(jsonPath("$.data.memberName").value("회원상세"))
            .andExpect(jsonPath("$.data.generation").value(11))
            .andExpect(jsonPath("$.data.attendances[0].sessionId").value(session.getId()))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("일정별 출결 목록 조회 성공")
    @Test
    void should_read_session_attendances() throws Exception {
        Cohort cohort = findCurrentCohort();
        Session session = createSession(cohort, "일정출결목록세션");
        Member member1 = createMember("admin-session-att-1@example.com", "목록회원1", "01066667777");
        Member member2 = createMember("admin-session-att-2@example.com", "목록회원2", "01077778888");

        createCohortMember(member1, cohort);
        createCohortMember(member2, cohort);
        createAttendance(session, member1, AttendanceStatus.PRESENT, 0, 0, "출석");
        createAttendance(session, member2, AttendanceStatus.ABSENT, 0, 50000, "결석");

        mockMvc.perform(get("/admin/attendances/sessions/{sessionId}", session.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.sessionId").value(session.getId()))
            .andExpect(jsonPath("$.data.sessionTitle").value("일정출결목록세션"))
            .andExpect(jsonPath("$.data.attendances.length()").value(2))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    private Cohort findCurrentCohort() {
        return cohortRepository.findByGeneration(new Generation(11))
            .orElseThrow(() -> new IllegalStateException("cohort 11 should exist"));
    }

    @SuppressWarnings("null")
    private Member createMember(String loginId, String name, String phone) {
        return memberRepository.save(Member.createMember(
            new LoginId(loginId),
            new Password("password"),
            new MemberName(name),
            new Phone(phone)
        ));
    }

    @SuppressWarnings("null")
    private CohortMember createCohortMember(Member member, Cohort cohort) {
        return cohortMemberRepository.save(CohortMember.createCohortMember(member, cohort, null, null));
    }

    @SuppressWarnings("null")
    private Session createSession(Cohort cohort, String title) {
        Session session = Session.builder()
            .cohort(cohort)
            .title(new SessionTitle(title))
            .sessionDate(LocalDate.now().plusDays(1))
            .sessionTime(LocalTime.of(19, 0))
            .sessionLocation(new SessionLocation("강남"))
            .sessionStatus(SessionStatus.SCHEDULED)
            .build();
        return sessionRepository.save(session);
    }

    @SuppressWarnings("null")
    private Attendance createAttendance(Session session, Member member, AttendanceStatus status, int lateMinutes, int penaltyAmount, String reason) {
        Attendance attendance = Attendance.builder()
            .session(session)
            .qrCode(null)
            .member(member)
            .attendanceStatus(status)
            .lateMinutes(new LateMinutes(lateMinutes))
            .penaltyAmount(new PenaltyAmount(penaltyAmount))
            .reason(new Reason(reason))
            .checkedInAt(LocalDateTime.now())
            .build();
        return attendanceRepository.save(attendance);
    }
}
