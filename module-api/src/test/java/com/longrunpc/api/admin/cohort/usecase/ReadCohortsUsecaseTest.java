package com.longrunpc.api.admin.cohort.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.api.admin.cohort.dto.response.CohortResponse;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;

@DisplayName("ReadCohortsUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class ReadCohortsUsecaseTest {

    @InjectMocks
    private ReadCohortsUsecase readCohortsUsecase;
    @Mock
    private CohortRepository cohortRepository;

    private Cohort cohort;

    @BeforeEach
    void setUp() {
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(11))
            .cohortName(new CohortName("11기"))
            .build();
    }

    @DisplayName("기수 목록 조회 성공")
    @Test
    void should_read_cohorts_when_valid_input() {
        // given
        given(cohortRepository.findAll()).willReturn(List.of(cohort));

        // when
        List<CohortResponse> result = readCohortsUsecase.execute();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(CohortResponse.of(cohort));
    }
}
