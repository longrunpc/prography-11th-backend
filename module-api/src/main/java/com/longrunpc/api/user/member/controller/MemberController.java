package com.longrunpc.api.user.member.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import com.longrunpc.common.response.ApiResponse;
import com.longrunpc.api.user.member.usecase.ReadMemberAttendanceSummaryUsecase;
import com.longrunpc.api.user.member.usecase.ReadMemberUsecase;
import com.longrunpc.api.user.member.dto.response.MemberAttendanceSummaryResponse;
import com.longrunpc.api.user.member.dto.response.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "사용자 멤버 API")
public class MemberController {

    private final ReadMemberUsecase readMemberUsecase;
    private final ReadMemberAttendanceSummaryUsecase readMemberAttendanceSummaryUsecase;

    @GetMapping("/{id}")
    @Operation(summary = "회원 조회", description = "회원 조회")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MEMBER_003: 회원을 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<MemberResponse>> readMember(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(readMemberUsecase.execute(id)));
    }

    @GetMapping("/{memberId}/attendance-summary")
    @Operation(summary = "내 출결 요약", description = "내 출결 요약")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "COHORT_001: 기수를 찾을 수 없습니다. / MEMBER_003: 회원을 찾을 수 없습니다. / COHORT_004: 기수 회원 정보를 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<MemberAttendanceSummaryResponse>> readMemberAttendanceSummary(@PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(readMemberAttendanceSummaryUsecase.execute(memberId)));
    }
}
