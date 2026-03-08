package com.longrunpc.api.admin.session.controller;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.AttendanceApplication;
import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;

@SpringBootTest(classes = AttendanceApplication.class)
@AutoConfigureMockMvc
@Transactional
class AdminQrCodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private QrCodeRepository qrCodeRepository;

    @DisplayName("QR 갱신 성공")
    @Test
    void should_reissue_qrcode() throws Exception {
        // given
        Session session = createSessionEntity("QR 갱신 대상");
        QrCode qrCode = Objects.requireNonNull(qrCodeRepository.save(QrCode.createQrCode(session)));

        // when & then
        mockMvc.perform(put("/admin/qrcodes/{qrCodeId}", qrCode.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.sessionId").value(session.getId()))
            .andExpect(jsonPath("$.data.hashValue").isString())
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("QR 갱신 실패 - 존재하지 않는 QR")
    @Test
    void should_fail_reissue_qrcode_when_not_found() throws Exception {
        // when & then
        mockMvc.perform(put("/admin/qrcodes/{qrCodeId}", 999999L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(SessionErrorCode.QR_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(SessionErrorCode.QR_NOT_FOUND.getMessage()));
    }

    @DisplayName("QR 갱신 실패 - 잘못된 qrCodeId 타입")
    @Test
    void should_fail_reissue_qrcode_when_id_is_invalid_type() throws Exception {
        // when & then
        mockMvc.perform(put("/admin/qrcodes/{qrCodeId}", "invalid-id"))
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
