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
public class AdminMemberController {

    private final RegisterMemberUsecase registerMemberUsecase;
    private final ReadMemberDashboardUsecase readMemberDashboardUsecase;
    private final ReadMemberDetailUsecase readMemberDetailUsecase;
    private final UpdateMemberUsecase updateMemberUsecase;
    private final WithdrawMemberUsecase withdrawMemberUsecase;

    @PostMapping
    public ResponseEntity<ApiResponse<MemberDetailResponse>> registerMember(
        @RequestBody RegisterMemberRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(registerMemberUsecase.execute(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MemberDashboardResponse>> readMemberDashboard(
        @ModelAttribute MemberDashboardRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(readMemberDashboardUsecase.execute(request)));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> readMemberDetail(@PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(readMemberDetailUsecase.execute(memberId)));
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> updateMember(@PathVariable Long memberId, @RequestBody UpdateMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success(updateMemberUsecase.execute(request, memberId)));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<WithdrawMemberResponse>> withdrawMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(withdrawMemberUsecase.execute(memberId)));
    }
}
