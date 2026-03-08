package com.longrunpc.api.user.attendance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.longrunpc.api.user.attendance.dto.request.RegisterAttendanceRequest;
import com.longrunpc.common.error.AttendanceErrorCode;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.error.SessionErrorCode;
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
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;

@SpringBootTest(classes = AttendanceApplication.class)
@AutoConfigureMockMvc
@Transactional
class AttendanceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private QrCodeRepository qrCodeRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private CohortMemberRepository cohortMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("QR 출석 체크 성공")
    @Test
    void should_register_attendance() throws Exception {
        // given
        Cohort cohort = findCurrentCohort();
        Member member = createMember("checkin-success@example.com", "출석성공", "01010101010");
        createCohortMember(member, cohort);
        Session session = createInProgressSession(cohort, "출석세션");
        QrCode qrCode = Objects.requireNonNull(qrCodeRepository.save(QrCode.createQrCode(session)));

        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            qrCode.getHashValue().getValue(),
            member.getId()
        );

        // when & then
        mockMvc.perform(post("/attendances")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.sessionId").value(session.getId()))
            .andExpect(jsonPath("$.data.memberId").value(member.getId()))
            .andExpect(jsonPath("$.data.status").isString())
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("QR 출석 체크 실패 - 유효하지 않은 QR")
    @Test
    void should_fail_register_attendance_when_qr_invalid() throws Exception {
        // given
        RegisterAttendanceRequest request = new RegisterAttendanceRequest("invalid-hash", 1L);

        // when & then
        mockMvc.perform(post("/attendances")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(SessionErrorCode.QR_INVALID.getCode()))
            .andExpect(jsonPath("$.error.message").value(SessionErrorCode.QR_INVALID.getMessage()));
    }

    @DisplayName("QR 출석 체크 실패 - 이미 출결 체크됨")
    @Test
    void should_fail_register_attendance_when_already_checked() throws Exception {
        // given
        Cohort cohort = findCurrentCohort();
        Member member = createMember("checkin-dup@example.com", "중복출석", "01020202020");
        createCohortMember(member, cohort);
        Session session = createInProgressSession(cohort, "중복체크세션");
        QrCode qrCode = Objects.requireNonNull(qrCodeRepository.save(QrCode.createQrCode(session)));
        createAttendance(session, qrCode, member, AttendanceStatus.LATE, 5, 5000);

        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            qrCode.getHashValue().getValue(),
            member.getId()
        );

        // when & then
        mockMvc.perform(post("/attendances")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(AttendanceErrorCode.ATTENDANCE_ALREADY_CHECKED.getCode()))
            .andExpect(jsonPath("$.error.message").value(AttendanceErrorCode.ATTENDANCE_ALREADY_CHECKED.getMessage()));
    }

    @DisplayName("QR 출석 체크 실패 - 기수 회원 정보 없음")
    @Test
    void should_fail_register_attendance_when_cohort_member_not_found() throws Exception {
        // given
        Cohort cohort = findCurrentCohort();
        Member member = createMember("checkin-no-cohort-member@example.com", "기수회원없음", "01030303030");
        Session session = createInProgressSession(cohort, "기수회원없음세션");
        QrCode qrCode = Objects.requireNonNull(qrCodeRepository.save(QrCode.createQrCode(session)));

        RegisterAttendanceRequest request = new RegisterAttendanceRequest(
            qrCode.getHashValue().getValue(),
            member.getId()
        );

        // when & then
        mockMvc.perform(post("/attendances")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(CohortErrorCode.COHORT_MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(CohortErrorCode.COHORT_MEMBER_NOT_FOUND.getMessage()));
    }

    @DisplayName("내 출결 기록 조회 성공")
    @Test
    void should_read_attendances() throws Exception {
        // given
        Cohort cohort = findCurrentCohort();
        Member member = createMember("attendance-read@example.com", "출결조회", "01040404040");
        Session session = createInProgressSession(cohort, "출결조회세션");
        QrCode qrCode = Objects.requireNonNull(qrCodeRepository.save(QrCode.createQrCode(session)));
        createAttendance(session, qrCode, member, AttendanceStatus.LATE, 3, 3000);

        // when & then
        mockMvc.perform(get("/attendances").param("memberId", member.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].memberId").doesNotExist())
            .andExpect(jsonPath("$.data[0].sessionId").value(session.getId()))
            .andExpect(jsonPath("$.data[0].sessionTitle").value("출결조회세션"))
            .andExpect(jsonPath("$.data[0].status").value("LATE"))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("내 출결 기록 조회 실패 - 존재하지 않는 회원")
    @Test
    void should_fail_read_attendances_when_member_not_found() throws Exception {
        // when & then
        mockMvc.perform(get("/attendances").param("memberId", "999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(MemberErrorCode.MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()));
    }

    @DisplayName("내 출결 기록 조회 실패 - 잘못된 memberId 타입")
    @Test
    void should_fail_read_attendances_when_member_id_is_invalid_type() throws Exception {
        // when & then
        mockMvc.perform(get("/attendances").param("memberId", "invalid-id"))
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

    private Session createInProgressSession(Cohort cohort, String title) {
        LocalTime now = LocalTime.now();
        Session session = Session.builder()
            .cohort(cohort)
            .title(new SessionTitle(title))
            .sessionDate(LocalDate.now())
            .sessionTime(LocalTime.of(now.getHour(), 0))
            .sessionLocation(new SessionLocation("강남"))
            .sessionStatus(SessionStatus.IN_PROGRESS)
            .build();
        return Objects.requireNonNull(sessionRepository.save(session));
    }

    private Attendance createAttendance(Session session, QrCode qrCode, Member member, AttendanceStatus status, int lateMinutes, int penaltyAmount) {
        Attendance attendance = Attendance.builder()
            .session(session)
            .qrCode(qrCode)
            .member(member)
            .attendanceStatus(status)
            .lateMinutes(new LateMinutes(lateMinutes))
            .penaltyAmount(new PenaltyAmount(penaltyAmount))
            .reason(new Reason("통합테스트"))
            .checkedInAt(LocalDateTime.now())
            .build();
        return Objects.requireNonNull(attendanceRepository.save(attendance));
    }
}
