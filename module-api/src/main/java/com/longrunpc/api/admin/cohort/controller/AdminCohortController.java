package com.longrunpc.api.admin.cohort.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.longrunpc.api.admin.cohort.usecase.ReadCohortDetailUsecase;
import com.longrunpc.api.admin.cohort.usecase.ReadCohortsUsecase;
import com.longrunpc.api.admin.cohort.dto.response.CohortDetailResponse;
import com.longrunpc.api.admin.cohort.dto.response.CohortResponse;
import com.longrunpc.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/cohorts")
public class AdminCohortController {

    private final ReadCohortsUsecase readCohortsUsecase;
    private final ReadCohortDetailUsecase readCohortDetailUsecase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CohortResponse>>> readCohorts() {
        return ResponseEntity.ok(ApiResponse.success(readCohortsUsecase.execute()));
    }

    @GetMapping("/{cohortId}")
    public ResponseEntity<ApiResponse<CohortDetailResponse>> readCohortDetails(@PathVariable Long cohortId) {
        return ResponseEntity.ok(ApiResponse.success(readCohortDetailUsecase.execute(cohortId)));
    }
}
