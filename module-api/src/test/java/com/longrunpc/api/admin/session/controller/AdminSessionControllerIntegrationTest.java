package com.longrunpc.api.admin.session.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longrunpc.api.AttendanceApplication;
import com.longrunpc.api.admin.session.dto.request.CreateSessionRequest;
import com.longrunpc.api.admin.session.dto.request.UpdateSessionRequest;
import com.longrunpc.api.admin.session.usecase.CreateSessionUsecase;
import com.longrunpc.api.admin.session.usecase.ReadSessionDetailsUsecase;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;

@SpringBootTest(classes = AttendanceApplication.class)
@AutoConfigureMockMvc
@Transactional
class AdminSessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private CreateSessionUsecase createSessionUsecase;

    @Autowired
    private ReadSessionDetailsUsecase readSessionDetailsUsecase;

    @DisplayName("일정 목록(관리자) 조회 성공")
    @Test
    void should_read_admin_sessions() throws Exception {
        // given
        ReflectionTestUtils.setField(readSessionDetailsUsecase, "currentGeneration", 11);

        // when & then
        mockMvc.perform(get("/admin/sessions")
                .param("dateFrom", LocalDate.now().minusDays(1).toString())
                .param("dateTo", LocalDate.now().plusDays(30).toString())
                .param("sessionStatus", "SCHEDULED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("일정 목록(관리자) 조회 실패 - 현재 기수 없음")
    @Test
    void should_fail_read_admin_sessions_when_current_cohort_not_found() throws Exception {
        // given
        ReflectionTestUtils.setField(readSessionDetailsUsecase, "currentGeneration", 999);

        // when & then
        mockMvc.perform(get("/admin/sessions")
                .param("dateFrom", LocalDate.now().minusDays(1).toString())
                .param("dateTo", LocalDate.now().plusDays(30).toString())
                .param("sessionStatus", "SCHEDULED"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(CohortErrorCode.COHORT_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(CohortErrorCode.COHORT_NOT_FOUND.getMessage()));
    }

    @DisplayName("일정 생성 성공")
    @Test
    void should_create_session() throws Exception {
        // given
        ReflectionTestUtils.setField(createSessionUsecase, "currentGeneration", 11);
        CreateSessionRequest request = new CreateSessionRequest(
            "세션 생성 테스트",
            LocalDate.now().plusDays(7),
            LocalTime.of(20, 0),
            "강남역"
        );

        // when & then
        mockMvc.perform(post("/admin/sessions")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.title").value("세션 생성 테스트"))
            .andExpect(jsonPath("$.data.sessionStatus").value("SCHEDULED"))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("일정 생성 실패 - 과거 일정")
    @Test
    void should_fail_create_session_when_past_datetime() throws Exception {
        // given
        ReflectionTestUtils.setField(createSessionUsecase, "currentGeneration", 11);
        CreateSessionRequest request = new CreateSessionRequest(
            "과거 일정",
            LocalDate.now().minusDays(1),
            LocalTime.of(10, 0),
            "강남역"
        );

        // when & then
        mockMvc.perform(post("/admin/sessions")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(GlobalErrorCode.INVALID_INPUT.getCode()))
            .andExpect(jsonPath("$.error.message").value(GlobalErrorCode.INVALID_INPUT.getMessage()));
    }

    @DisplayName("일정 수정 성공")
    @Test
    void should_update_session() throws Exception {
        // given
        Session session = createSessionEntity("수정 전 일정");
        UpdateSessionRequest request = new UpdateSessionRequest(
            "수정 후 일정",
            LocalDate.now().plusDays(8),
            LocalTime.of(21, 0),
            "선릉역",
            SessionStatus.IN_PROGRESS
        );

        // when & then
        mockMvc.perform(put("/admin/sessions/{id}", session.getId())
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(session.getId()))
            .andExpect(jsonPath("$.data.title").value("수정 후 일정"))
            .andExpect(jsonPath("$.data.location").value("선릉역"))
            .andExpect(jsonPath("$.data.sessionStatus").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("일정 수정 실패 - 존재하지 않는 일정")
    @Test
    void should_fail_update_session_when_not_found() throws Exception {
        // given
        UpdateSessionRequest request = new UpdateSessionRequest(
            "수정",
            null,
            null,
            null,
            null
        );

        // when & then
        mockMvc.perform(put("/admin/sessions/{id}", 999999L)
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(SessionErrorCode.SESSION_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(SessionErrorCode.SESSION_NOT_FOUND.getMessage()));
    }

    @DisplayName("일정 수정 실패 - 이미 취소된 일정")
    @Test
    void should_fail_update_session_when_already_cancelled() throws Exception {
        // given
        Session session = createSessionEntity("취소된 일정");
        session.cancel();
        UpdateSessionRequest request = new UpdateSessionRequest(
            "수정 시도",
            null,
            null,
            null,
            null
        );

        // when & then
        mockMvc.perform(put("/admin/sessions/{id}", session.getId())
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(SessionErrorCode.SESSION_ALREADY_CANCELLED.getCode()))
            .andExpect(jsonPath("$.error.message").value(SessionErrorCode.SESSION_ALREADY_CANCELLED.getMessage()));
    }

    @DisplayName("일정 수정 실패 - 잘못된 id 타입")
    @Test
    void should_fail_update_session_when_id_is_invalid_type() throws Exception {
        // given
        UpdateSessionRequest request = new UpdateSessionRequest(
            "수정",
            null,
            null,
            null,
            null
        );

        // when & then
        mockMvc.perform(put("/admin/sessions/{id}", "invalid-id")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(GlobalErrorCode.INTERNAL_ERROR.getCode()))
            .andExpect(jsonPath("$.error.message").value(GlobalErrorCode.INTERNAL_ERROR.getMessage()));
    }

    @DisplayName("일정 삭제 성공")
    @Test
    void should_delete_session() throws Exception {
        // given
        Session session = createSessionEntity("삭제 대상 일정");

        // when & then
        mockMvc.perform(delete("/admin/sessions/{id}", session.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(session.getId()))
            .andExpect(jsonPath("$.data.sessionStatus").value("CANCELLED"))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("일정 삭제 실패 - 존재하지 않는 일정")
    @Test
    void should_fail_delete_session_when_not_found() throws Exception {
        // when & then
        mockMvc.perform(delete("/admin/sessions/{id}", 999999L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(SessionErrorCode.SESSION_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(SessionErrorCode.SESSION_NOT_FOUND.getMessage()));
    }

    @DisplayName("일정 삭제 실패 - 이미 취소된 일정")
    @Test
    void should_fail_delete_session_when_already_cancelled() throws Exception {
        // given
        Session session = createSessionEntity("이미 취소된 삭제 대상");
        session.cancel();

        // when & then
        mockMvc.perform(delete("/admin/sessions/{id}", session.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(SessionErrorCode.SESSION_ALREADY_CANCELLED.getCode()))
            .andExpect(jsonPath("$.error.message").value(SessionErrorCode.SESSION_ALREADY_CANCELLED.getMessage()));
    }

    @DisplayName("일정 삭제 실패 - 잘못된 id 타입")
    @Test
    void should_fail_delete_session_when_id_is_invalid_type() throws Exception {
        // when & then
        mockMvc.perform(delete("/admin/sessions/{id}", "invalid-id"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(GlobalErrorCode.INTERNAL_ERROR.getCode()))
            .andExpect(jsonPath("$.error.message").value(GlobalErrorCode.INTERNAL_ERROR.getMessage()));
    }

    private Session createSessionEntity(String title) {
        Cohort cohort = cohortRepository.findByGeneration(new Generation(11))
            .orElseThrow(() -> new IllegalStateException("cohort 11 should exist"));

        Session session = Session.createSession(
            cohort,
            new SessionTitle(title),
            LocalDate.now().plusDays(5),
            LocalTime.of(19, 30),
            new SessionLocation("강남")
        );
        return Objects.requireNonNull(sessionRepository.save(session));
    }
}
