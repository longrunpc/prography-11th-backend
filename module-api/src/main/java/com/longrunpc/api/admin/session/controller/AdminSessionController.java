package com.longrunpc.api.admin.session.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.longrunpc.api.admin.session.usecase.CancelSessionUsecase;
import com.longrunpc.api.admin.session.usecase.CreateQrCodeUsecase;
import com.longrunpc.api.admin.session.usecase.CreateSessionUsecase;
import com.longrunpc.api.admin.session.usecase.ReadSessionDetailsUsecase;
import com.longrunpc.api.admin.session.usecase.UpdateSessionUsecase;
import com.longrunpc.api.admin.session.dto.request.CreateSessionRequest;
import com.longrunpc.api.admin.session.dto.request.ReadSessionDetailsRequest;
import com.longrunpc.api.admin.session.dto.request.UpdateSessionRequest;
import com.longrunpc.api.admin.session.dto.response.QrCodeResponse;
import com.longrunpc.api.admin.session.dto.response.SessionDetailResponse;
import com.longrunpc.common.response.ApiResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/sessions")
public class AdminSessionController {

    private final ReadSessionDetailsUsecase readSessionDetailsUsecase;
    private final CreateSessionUsecase createSessionUsecase;
    private final UpdateSessionUsecase updateSessionUsecase;
    private final CancelSessionUsecase cancelSessionUsecase;
    private final CreateQrCodeUsecase createQrCodeUsecase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SessionDetailResponse>>> readSessionDetails(@ModelAttribute ReadSessionDetailsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(readSessionDetailsUsecase.execute(request)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SessionDetailResponse>> createSession(@RequestBody CreateSessionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(createSessionUsecase.execute(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionDetailResponse>> updateSession(@PathVariable Long sessionId, @RequestBody UpdateSessionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(updateSessionUsecase.execute(request, sessionId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionDetailResponse>> cancelSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success(cancelSessionUsecase.execute(sessionId)));
    }

    @PostMapping("/{sessionId}/qrcodes")
    public ResponseEntity<ApiResponse<QrCodeResponse>> createQrCode(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success(createQrCodeUsecase.excute(sessionId)));
    }
}
