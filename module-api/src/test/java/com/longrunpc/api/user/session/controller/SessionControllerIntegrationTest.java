package com.longrunpc.api.user.session.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.AttendanceApplication;
import com.longrunpc.api.user.session.usecase.ReadValidSessionsUsecase;
import com.longrunpc.common.error.CohortErrorCode;
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
class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private ReadValidSessionsUsecase readValidSessionsUsecase;

    @DisplayName("일정 목록(회원) 조회 성공 - 취소 일정은 제외된다")
    @Test
    void should_read_valid_sessions_excluding_cancelled() throws Exception {
        // given
        ReflectionTestUtils.setField(readValidSessionsUsecase, "currentGeneration", 11);
        Cohort cohort = findCurrentCohort();

        Session scheduled = Objects.requireNonNull(sessionRepository.save(
            Session.createSession(
                cohort,
                new SessionTitle("회원 목록 노출 일정"),
                LocalDate.now().plusDays(3),
                LocalTime.of(18, 0),
                new SessionLocation("강남")
            )
        ));

        Session cancelled = Objects.requireNonNull(sessionRepository.save(
            Session.createSession(
                cohort,
                new SessionTitle("회원 목록 제외 일정"),
                LocalDate.now().plusDays(4),
                LocalTime.of(19, 0),
                new SessionLocation("잠실")
            )
        ));
        cancelled.cancel();

        // when & then
        mockMvc.perform(get("/sessions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[*].id", hasItem(scheduled.getId().intValue())))
            .andExpect(jsonPath("$.data[*].id", not(hasItem(cancelled.getId().intValue()))))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("일정 목록(회원) 조회 실패 - 현재 기수 없음")
    @Test
    void should_fail_read_valid_sessions_when_current_cohort_not_found() throws Exception {
        // given
        ReflectionTestUtils.setField(readValidSessionsUsecase, "currentGeneration", 999);

        // when & then
        mockMvc.perform(get("/sessions"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(CohortErrorCode.COHORT_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(CohortErrorCode.COHORT_NOT_FOUND.getMessage()));
    }

    private Cohort findCurrentCohort() {
        return cohortRepository.findByGeneration(new Generation(11))
            .orElseThrow(() -> new IllegalStateException("cohort 11 should exist"));
    }
}
