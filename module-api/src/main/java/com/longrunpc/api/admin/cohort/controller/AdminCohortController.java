package com.longrunpc.api.admin.cohort.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.longrunpc.api.admin.cohort.usecase.ReadCohortDetailUsecase;
import com.longrunpc.api.admin.cohort.usecase.ReadCohortsUsecase;
import com.longrunpc.api.admin.cohort.dto.response.CohortDetailResponse;
import com.longrunpc.api.admin.cohort.dto.response.CohortResponse;
import com.longrunpc.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/cohorts")
@Tag(name = "Admin Cohort", description = "관리자 기수 API")
public class AdminCohortController {

    private final ReadCohortsUsecase readCohortsUsecase;
    private final ReadCohortDetailUsecase readCohortDetailUsecase;

    @GetMapping
    @Operation(summary = "기수 목록", description = "기수 목록")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<List<CohortResponse>>> readCohorts() {
        return ResponseEntity.ok(ApiResponse.success(readCohortsUsecase.execute()));
    }

    @GetMapping("/{cohortId}")
    @Operation(summary = "기수 상세", description = "기수 상세")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "COHORT_001: 기수를 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<CohortDetailResponse>> readCohortDetails(@PathVariable Long cohortId) {
        return ResponseEntity.ok(ApiResponse.success(readCohortDetailUsecase.execute(cohortId)));
    }
}
