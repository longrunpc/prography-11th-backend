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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/attendances")
public class AdminAttendanceController {

    private final ReadSessionAttendanceUsecase readSessionAttendanceUsecase;
    private final ReadMemberAttendanceDetailUsecase readMemberAttendanceDetailUsecase;
    private final AdminRegisterAttendanceUsecase adminRegisterAttendanceUsecase;
    private final UpdateAttendanceUsecase updateAttendanceUsecase;
    private final ReadAttendanceSummaryUsecase readAttendanceSummaryUsecase;

    @PostMapping
    public ResponseEntity<ApiResponse<AdminAttendanceResponse>> adminRegisterAttendance(@RequestBody AdminRegisterAttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminRegisterAttendanceUsecase.execute(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminAttendanceResponse>> updateAttendance(@PathVariable Long id, @RequestBody UpdateAttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(updateAttendanceUsecase.execute(request, id)));
    }

    @GetMapping("/sessions/{sessionId}/summary")
    public ResponseEntity<ApiResponse<List<AdminMemberAttendanceSummaryResponse>>> readAttendanceSummary(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success(readAttendanceSummaryUsecase.execute(sessionId)));
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<ApiResponse<MemberAttendanceResponse>> readMemberAttendanceDetail(@PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(readMemberAttendanceDetailUsecase.execute(memberId)));
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<SessionAttendanceResponse>> readSessionAttendance(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success(readSessionAttendanceUsecase.execute(sessionId)));
    }
}
