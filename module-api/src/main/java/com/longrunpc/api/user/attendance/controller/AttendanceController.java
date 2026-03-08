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
public class AttendanceController {
    
    private final RegisterAttendanceUsecase registerAttendanceUsecase;
    private final ReadAttendancesUsecase readAttendancesUsecase;

    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceDetailResponse>> registerAttendance(@Valid @RequestBody RegisterAttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registerAttendanceUsecase.execute(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> readAttendances(@RequestParam Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(readAttendancesUsecase.execute(memberId)));
    }
}
