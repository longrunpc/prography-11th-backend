package com.longrunpc.api.user.session.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

import com.longrunpc.api.user.session.dto.response.SessionResponse;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;

@DisplayName("ReadValidSessionsUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class ReadValidSessionsUsecaseTest {

    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private CohortRepository cohortRepository;
    @InjectMocks
    private ReadValidSessionsUsecase readValidSessionsUsecase;

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    private List<Session> sessions;
    private Cohort cohort;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(readValidSessionsUsecase, "currentGeneration", currentGeneration);
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(currentGeneration))
            .cohortName(new CohortName("11기"))
            .build();
        sessions = List.of(
            Session.builder()
                .id(1L)
                .cohort(cohort)
                .title(new SessionTitle("test"))
                .sessionDate(LocalDate.now().plusDays(1))
                .sessionTime(LocalTime.now().plusHours(1))
                .sessionLocation(new SessionLocation("강남역"))
                .sessionStatus(SessionStatus.SCHEDULED)
                .build()
        );
    }

    @DisplayName("유효한 세션 목록 조회 성공")
    @Test
    void should_read_valid_sessions_when_valid_input() {
        // given
        given(sessionRepository.findByCohortIdAndSessionStatusNot(cohort.getId(), SessionStatus.CANCELLED)).willReturn(sessions);
        given(cohortRepository.findByGeneration(new Generation(currentGeneration))).willReturn(Optional.of(cohort));

        // when
        List<SessionResponse> result = readValidSessionsUsecase.execute();

        // then
        assertThat(result).hasSize(sessions.size());
        assertThat(result).isEqualTo(sessions.stream().map(SessionResponse::of).toList());
    }
}
