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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/cohort-members")
@Tag(name = "Admin Cohort Member", description = "관리자 기수 멤버 API")
public class AdminCohortMemberController {

    private final ReadCohortMemberDepositHistoryUsecase readCohortMemberDepositHistoryUsecase;

    @GetMapping("/{cohortMemberId}/deposits")
    @Operation(summary = "보증금 이력", description = "보증금 이력")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "COHORT_004: 기수 회원 정보를 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<List<CohortMemberDepositHistoryResponse>>> readCohortMemberDepositHistory(@PathVariable Long cohortMemberId) {
        return ResponseEntity.ok(ApiResponse.success(readCohortMemberDepositHistoryUsecase.execute(cohortMemberId)));
    }
}
