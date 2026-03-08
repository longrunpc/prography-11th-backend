package com.longrunpc.api.admin.cohort.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.AttendanceApplication;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.Part;
import com.longrunpc.domain.cohort.entity.Team;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.PartRepository;
import com.longrunpc.domain.cohort.repository.TeamRepository;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.PartName;
import com.longrunpc.domain.cohort.vo.TeamName;

@SpringBootTest(classes = AttendanceApplication.class)
@AutoConfigureMockMvc
@Transactional
class AdminCohortControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private TeamRepository teamRepository;

    @DisplayName("기수 목록 조회 성공")
    @Test
    void should_read_cohorts() throws Exception {
        // given
        Cohort cohort = createCohort(31, "31기-통합테스트");

        // when & then
        mockMvc.perform(get("/admin/cohorts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[*].id", hasItem(cohort.getId().intValue())))
            .andExpect(jsonPath("$.data[*].generation", hasItem(31)))
            .andExpect(jsonPath("$.data[*].name", hasItem("31기-통합테스트")))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("기수 상세 조회 성공")
    @Test
    void should_read_cohort_detail() throws Exception {
        // given
        Cohort cohort = createCohort(32, "32기-통합테스트");
        Part part = createPart(cohort, "백엔드");
        Team team = createTeam(cohort, "3팀");

        // when & then
        mockMvc.perform(get("/admin/cohorts/{cohortId}", cohort.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(cohort.getId()))
            .andExpect(jsonPath("$.data.generation").value(32))
            .andExpect(jsonPath("$.data.name").value("32기-통합테스트"))
            .andExpect(jsonPath("$.data.parts[*].id", hasItem(part.getId().intValue())))
            .andExpect(jsonPath("$.data.parts[*].name", hasItem("백엔드")))
            .andExpect(jsonPath("$.data.teams[*].id", hasItem(team.getId().intValue())))
            .andExpect(jsonPath("$.data.teams[*].name", hasItem("3팀")))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("기수 상세 조회 실패 - 존재하지 않는 기수")
    @Test
    void should_fail_when_cohort_not_found() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/cohorts/{cohortId}", 999999L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(CohortErrorCode.COHORT_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(CohortErrorCode.COHORT_NOT_FOUND.getMessage()));
    }

    private Cohort createCohort(int generation, String cohortName) {
        return Objects.requireNonNull(cohortRepository.save(Cohort.builder()
            .generation(new Generation(generation))
            .cohortName(new CohortName(cohortName))
            .build()));
    }

    private Part createPart(Cohort cohort, String partName) {
        return Objects.requireNonNull(partRepository.save(Part.builder()
            .cohort(cohort)
            .partName(new PartName(partName))
            .build()));
    }

    private Team createTeam(Cohort cohort, String teamName) {
        return Objects.requireNonNull(teamRepository.save(Team.builder()
            .cohort(cohort)
            .teamName(new TeamName(teamName))
            .build()));
    }
}
