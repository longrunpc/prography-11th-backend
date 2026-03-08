package com.longrunpc.api.admin.session.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.api.admin.session.dto.response.AttendanceSummaryResponse;
import com.longrunpc.api.admin.session.dto.response.SessionDetailResponse;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.entity.SessionStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;

@DisplayName("CancelSessionUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class CancelSessionUsecaseTest {
    @InjectMocks
    private CancelSessionUsecase cancelSessionUsecase;
    @Mock
    private SessionRepository sessionRepository;

    private Session session;
    private Cohort cohort;
    @BeforeEach
    void setUp() {
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(11))
            .cohortName(new CohortName("11기"))
            .build();
        session = Session.builder()
            .id(1L)
            .cohort(cohort)
            .title(new SessionTitle("test"))
            .sessionDate(LocalDate.now())
            .sessionTime(LocalTime.now())
            .sessionLocation(new SessionLocation("test"))
            .sessionStatus(SessionStatus.SCHEDULED)
            .build();
    }
    
    @DisplayName("세션 취소 성공")
    @Test
    void should_cancel_session_when_valid_input() {
        // given
        given(sessionRepository.findById(1L)).willReturn(Optional.of(session));
        given(sessionRepository.save(any(Session.class))).willReturn(session);
    
        // when
        SessionDetailResponse response = cancelSessionUsecase.execute(1L);

        // then
        assertThat(response.id()).isEqualTo(session.getId());
        assertThat(response.title()).isEqualTo(session.getTitle().getValue());
        assertThat(response.date()).isEqualTo(session.getSessionDate());
        assertThat(response.time()).isEqualTo(session.getSessionTime());
        assertThat(response.location()).isEqualTo(session.getSessionLocation().getValue());
        assertThat(response.sessionStatus()).isEqualTo(session.getSessionStatus());
        assertThat(response.attendanceSummary()).isEqualTo(AttendanceSummaryResponse.of(new ArrayList<>()));
        assertThat(response.qrActive()).isEqualTo(true);
    }

    @DisplayName("세션 취소 실패 - 세션 존재하지 않음")
    @Test
    void should_fail_cancel_session_when_session_not_found() {
        // given
        given(sessionRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cancelSessionUsecase.execute(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.SESSION_NOT_FOUND.getMessage());
    }

    @DisplayName("세션 취소 실패 - 세션 이미 취소됨")
    @Test
    void should_fail_cancel_session_when_session_already_cancelled() {
        // given
        given(sessionRepository.findById(1L)).willReturn(Optional.of(session));
        session.cancel();

        // when & then
        assertThatThrownBy(() -> cancelSessionUsecase.execute(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.SESSION_ALREADY_CANCELLED.getMessage());
    }
}
