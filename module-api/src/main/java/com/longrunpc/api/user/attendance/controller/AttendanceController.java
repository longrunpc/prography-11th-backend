package com.longrunpc.api.user.attendance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.longrunpc.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import com.longrunpc.api.user.attendance.dto.request.RegisterAttendanceRequest;
import com.longrunpc.api.user.attendance.dto.response.AttendanceDetailResponse;
import com.longrunpc.api.user.attendance.dto.response.AttendanceResponse;
import com.longrunpc.api.user.attendance.usecase.ReadAttendancesUsecase;
import com.longrunpc.api.user.attendance.usecase.RegisterAttendanceUsecase;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendances")
@Tag(name = "Attendance", description = "사용자 출석 API")
public class AttendanceController {
    
    private final RegisterAttendanceUsecase registerAttendanceUsecase;
    private final ReadAttendancesUsecase readAttendancesUsecase;

    @PostMapping
    @Operation(summary = "QR 출석 체크", description = "QR 출석 체크")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "SESSION_005: 유효하지 않은 QR 코드입니다. / SESSION_006: 만료된 QR 코드입니다. / SESSION_003: 진행 중인 일정이 아닙니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "MEMBER_002: 탈퇴한 회원입니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "SESSION_001: 일정을 찾을 수 없습니다. / MEMBER_003: 회원을 찾을 수 없습니다. / COHORT_001: 기수를 찾을 수 없습니다. / COHORT_004: 기수 회원 정보를 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "ATTENDANCE_002: 이미 출결 체크가 완료되었습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<AttendanceDetailResponse>> registerAttendance(@Valid @RequestBody RegisterAttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registerAttendanceUsecase.execute(request)));
    }

    @GetMapping
    @Operation(summary = "내 출결 기록", description = "내 출결 기록")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "MEMBER_003: 회원을 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> readAttendances(@RequestParam Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(readAttendancesUsecase.execute(memberId)));
    }
}
