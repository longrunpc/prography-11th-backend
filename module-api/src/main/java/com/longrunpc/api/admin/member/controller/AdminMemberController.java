package com.longrunpc.api.admin.member.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import com.longrunpc.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import com.longrunpc.api.admin.member.dto.request.MemberDashboardRequest;
import com.longrunpc.api.admin.member.dto.request.RegisterMemberRequest;
import com.longrunpc.api.admin.member.dto.request.UpdateMemberRequest;
import com.longrunpc.api.admin.member.dto.response.MemberDashboardResponse;
import com.longrunpc.api.admin.member.dto.response.MemberDetailResponse;
import com.longrunpc.api.admin.member.dto.response.WithdrawMemberResponse;
import com.longrunpc.api.admin.member.usecase.ReadMemberDashboardUsecase;
import com.longrunpc.api.admin.member.usecase.ReadMemberDetailUsecase;
import com.longrunpc.api.admin.member.usecase.RegisterMemberUsecase;
import com.longrunpc.api.admin.member.usecase.UpdateMemberUsecase;
import com.longrunpc.api.admin.member.usecase.WithdrawMemberUsecase;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/members")
@Tag(name = "Admin Member", description = "관리자 멤버 관리 API")
public class AdminMemberController {

    private final RegisterMemberUsecase registerMemberUsecase;
    private final ReadMemberDashboardUsecase readMemberDashboardUsecase;
    private final ReadMemberDetailUsecase readMemberDetailUsecase;
    private final UpdateMemberUsecase updateMemberUsecase;
    private final WithdrawMemberUsecase withdrawMemberUsecase;

    @PostMapping
    @Operation(summary = "회원 등록", description = "회원 등록")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "COHORT_001: 기수를 찾을 수 없습니다. / COHORT_002: 파트를 찾을 수 없습니다. / COHORT_003: 팀을 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "MEMBER_004: 이미 사용 중인 로그인 아이디입니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<MemberDetailResponse>> registerMember(
        @RequestBody RegisterMemberRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(registerMemberUsecase.execute(request)));
    }

    @GetMapping
    @Operation(summary = "회원 대시보드", description = "회원 대시보드")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<MemberDashboardResponse>> readMemberDashboard(
        @ModelAttribute MemberDashboardRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(readMemberDashboardUsecase.execute(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "회원 상세", description = "회원 상세")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MEMBER_003: 회원을 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<MemberDetailResponse>> readMemberDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(readMemberDetailUsecase.execute(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "회원 수정", description = "회원 수정")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MEMBER_003: 회원을 찾을 수 없습니다. / COHORT_001: 기수를 찾을 수 없습니다. / COHORT_002: 파트를 찾을 수 없습니다. / COHORT_003: 팀을 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<MemberDetailResponse>> updateMember(@PathVariable Long id, @RequestBody UpdateMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success(updateMemberUsecase.execute(request, id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "MEMBER_005: 이미 탈퇴한 회원입니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MEMBER_003: 회원을 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<WithdrawMemberResponse>> withdrawMember(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(withdrawMemberUsecase.execute(id)));
    }
}
