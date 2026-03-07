package com.longrunpc.api.admin.cohort.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.PartRepository;
import com.longrunpc.domain.cohort.repository.TeamRepository;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.PartName;
import com.longrunpc.domain.cohort.vo.TeamName;
import com.longrunpc.api.admin.cohort.dto.response.CohortDetailResponse;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.Part;
import com.longrunpc.domain.cohort.entity.Team;

@DisplayName("ReadCohortDetailUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class ReadCohortDetailUsecaseTest {
    @InjectMocks
    private ReadCohortDetailUsecase readCohortDetailUsecase;
    @Mock
    private CohortRepository cohortRepository;
    @Mock
    private PartRepository partRepository;
    @Mock
    private TeamRepository teamRepository;
    
    private Cohort cohort;
    private List<Part> parts;
    private List<Team> teams;

    @BeforeEach
    void setUp() {
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(11))
            .cohortName(new CohortName("11기"))
            .build();
        parts = List.of(
            Part.builder()
                .id(1L)
                .partName(new PartName("test"))
                .cohort(cohort)
                .build()
        );
        teams = List.of(
            Team.builder()
                .id(1L)
                .teamName(new TeamName("test"))
                .cohort(cohort)
                .build()
        );
    }

    @DisplayName("기수 상세 조회 성공")
    @Test
    void should_read_cohort_detail_when_valid_input() {
        // given
        given(cohortRepository.findById(1L)).willReturn(Optional.of(cohort));
        given(partRepository.findByCohortId(1L)).willReturn(parts);
        given(teamRepository.findByCohortId(1L)).willReturn(teams);

        // when
        CohortDetailResponse result = readCohortDetailUsecase.execute(1L);

        // then
        assertThat(result).isEqualTo(CohortDetailResponse.of(cohort, parts, teams));
    }
}
