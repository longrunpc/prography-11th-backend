package com.longrunpc.api.admin.attendance.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.longrunpc.api.admin.attendance.dto.request.AdminRegisterAttendanceRequest;
import com.longrunpc.api.admin.attendance.dto.request.UpdateAttendanceRequest;
import com.longrunpc.api.admin.attendance.dto.response.AdminAttendanceResponse;
import com.longrunpc.api.admin.attendance.dto.response.AdminMemberAttendanceSummaryResponse;
import com.longrunpc.api.admin.attendance.dto.response.MemberAttendanceResponse;
import com.longrunpc.api.admin.attendance.dto.response.SessionAttendanceResponse;
import com.longrunpc.api.admin.attendance.usecase.AdminRegisterAttendanceUsecase;
import com.longrunpc.api.admin.attendance.usecase.ReadAttendanceSummaryUsecase;
import com.longrunpc.api.admin.attendance.usecase.ReadMemberAttendanceDetailUsecase;
import com.longrunpc.api.admin.attendance.usecase.ReadSessionAttendanceUsecase;
import com.longrunpc.api.admin.attendance.usecase.UpdateAttendanceUsecase;
import com.longrunpc.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/attendances")
@Tag(name = "Admin Attendance", description = "관리자 출석 API")
public class AdminAttendanceController {

    private final ReadSessionAttendanceUsecase readSessionAttendanceUsecase;
    private final ReadMemberAttendanceDetailUsecase readMemberAttendanceDetailUsecase;
    private final AdminRegisterAttendanceUsecase adminRegisterAttendanceUsecase;
    private final UpdateAttendanceUsecase updateAttendanceUsecase;
    private final ReadAttendanceSummaryUsecase readAttendanceSummaryUsecase;

    @PostMapping
    @Operation(summary = "출결 등록", description = "출결 등록")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "SESSION_001: 일정을 찾을 수 없습니다. / MEMBER_003: 회원을 찾을 수 없습니다. / COHORT_004: 기수 회원 정보를 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "ATTENDANCE_002: 이미 출결 체크가 완료되었습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<AdminAttendanceResponse>> adminRegisterAttendance(@RequestBody AdminRegisterAttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminRegisterAttendanceUsecase.execute(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "출결 수정", description = "출결 수정")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "ATTENDANCE_001: 출결 기록을 찾을 수 없습니다. / COHORT_004: 기수 회원 정보를 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<AdminAttendanceResponse>> updateAttendance(@PathVariable Long id, @RequestBody UpdateAttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(updateAttendanceUsecase.execute(request, id)));
    }

    @GetMapping("/sessions/{sessionId}/summary")
    @Operation(summary = "일정별 출결 요약", description = "일정별 출결 요약")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "COHORT_001: 기수를 찾을 수 없습니다. / SESSION_001: 일정을 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<List<AdminMemberAttendanceSummaryResponse>>> readAttendanceSummary(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success(readAttendanceSummaryUsecase.execute(sessionId)));
    }

    @GetMapping("/members/{memberId}")
    @Operation(summary = "회원 출결 상세", description = "회원 출결 상세")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MEMBER_003: 회원을 찾을 수 없습니다. / COHORT_004: 기수 회원 정보를 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<MemberAttendanceResponse>> readMemberAttendanceDetail(@PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(readMemberAttendanceDetailUsecase.execute(memberId)));
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "일정별 출결 목록", description = "일정별 출결 목록")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "SESSION_001: 일정을 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<SessionAttendanceResponse>> readSessionAttendance(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success(readSessionAttendanceUsecase.execute(sessionId)));
    }
}
