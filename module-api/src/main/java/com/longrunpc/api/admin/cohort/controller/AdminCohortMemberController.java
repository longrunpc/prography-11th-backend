package com.longrunpc.api.admin.cohort.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.longrunpc.api.admin.cohort.dto.response.CohortMemberDepositHistoryResponse;
import com.longrunpc.api.admin.cohort.usecase.ReadCohortMemberDepositHistoryUsecase;
import com.longrunpc.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/cohort-members")
public class AdminCohortMemberController {

    private final ReadCohortMemberDepositHistoryUsecase readCohortMemberDepositHistoryUsecase;

    @GetMapping("/{cohortMemberId}/deposits")
    public ResponseEntity<ApiResponse<List<CohortMemberDepositHistoryResponse>>> readCohortMemberDepositHistory(@PathVariable Long cohortMemberId) {
        return ResponseEntity.ok(ApiResponse.success(readCohortMemberDepositHistoryUsecase.execute(cohortMemberId)));
    }
}
