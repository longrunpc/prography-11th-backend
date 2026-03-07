package com.longrunpc.api.admin.session.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import com.longrunpc.api.admin.session.dto.request.CreateSessionRequest;
import com.longrunpc.api.admin.session.dto.response.AttendanceSummaryResponse;
import com.longrunpc.api.admin.session.dto.response.SessionDetailResponse;
import com.longrunpc.common.constant.qrCode.QrCodeConstants;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.QrCodeHashValue;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import com.longrunpc.domain.session.entity.SessionStatus;

@DisplayName("CreateSessionUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class CreateSessionUsecaseTest {
    @InjectMocks
    private CreateSessionUsecase createSessionUsecase;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private QrCodeRepository qrCodeRepository;
    @Mock
    private CohortRepository cohortRepository;

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    private Cohort cohort;
    private Session session;
    private QrCode qrCode;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    
    @BeforeEach
    void setUp() {
        sessionDate = LocalDate.now().plusDays(1);
        sessionTime = LocalTime.now().plusHours(1);
        ReflectionTestUtils.setField(createSessionUsecase, "currentGeneration", currentGeneration);
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(currentGeneration))
            .cohortName(new CohortName("11기"))
            .build();
        session = Session.builder()
            .id(1L)
            .cohort(cohort)
            .title(new SessionTitle("test"))
            .sessionDate(sessionDate)
            .sessionTime(sessionTime)
            .sessionLocation(new SessionLocation("강남역"))
            .sessionStatus(SessionStatus.SCHEDULED)
            .build();
        qrCode = QrCode.builder()
            .id(1L)
            .session(session)
            .hashValue(new QrCodeHashValue("hashValue"))
            .expiresAt(LocalDateTime.of(sessionDate, sessionTime).plusHours(QrCodeConstants.EXPIRATION_HOURS))
            .build();
    }

    @DisplayName("세션 생성 성공")
    @Test
    void should_create_session_when_valid_input() {
        // given
        given(cohortRepository.findByGeneration(new Generation(currentGeneration))).willReturn(Optional.of(cohort));
        given(sessionRepository.save(any(Session.class))).willReturn(session);
        given(qrCodeRepository.save(any(QrCode.class))).willReturn(qrCode);

        // when
        SessionDetailResponse response = createSessionUsecase.execute(new CreateSessionRequest("test", LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), "강남역"));

        // then
        assertThat(response.id()).isEqualTo(session.getId());
        assertThat(response.cohortId()).isEqualTo(cohort.getId());
        assertThat(response.title()).isEqualTo(session.getTitle().getValue());
        assertThat(response.date()).isEqualTo(session.getSessionDate());
        assertThat(response.time()).isEqualTo(session.getSessionTime());
        assertThat(response.location()).isEqualTo(session.getSessionLocation().getValue());
        assertThat(response.sessionStatus()).isEqualTo(session.getSessionStatus());
        assertThat(response.attendanceSummary()).isEqualTo(AttendanceSummaryResponse.of(new ArrayList<>()));
        assertThat(response.qrActive()).isEqualTo(true);
    }

}
