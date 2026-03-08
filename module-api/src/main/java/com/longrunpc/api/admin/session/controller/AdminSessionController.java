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

import jakarta.validation.Valid;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/sessions")
@Tag(name = "Admin Session", description = "관리자 세션 관리 API")
public class AdminSessionController {

    private final ReadSessionDetailsUsecase readSessionDetailsUsecase;
    private final CreateSessionUsecase createSessionUsecase;
    private final UpdateSessionUsecase updateSessionUsecase;
    private final CancelSessionUsecase cancelSessionUsecase;
    private final CreateQrCodeUsecase createQrCodeUsecase;

    @GetMapping
    @Operation(summary = "일정 목록 (관리자)", description = "일정 목록 (관리자)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "COHORT_001: 기수를 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<List<SessionDetailResponse>>> readSessionDetails(@ModelAttribute ReadSessionDetailsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(readSessionDetailsUsecase.execute(request)));
    }

    @PostMapping
    @Operation(summary = "일정 생성", description = "일정 생성")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "COMMON_001: 입력값이 올바르지 않습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "COHORT_001: 기수를 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<SessionDetailResponse>> createSession(@Valid @RequestBody CreateSessionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(createSessionUsecase.execute(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "일정 수정", description = "일정 수정")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "SESSION_001: 일정을 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "SESSION_002: 이미 취소된 일정입니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<SessionDetailResponse>> updateSession(@PathVariable Long id, @RequestBody UpdateSessionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(updateSessionUsecase.execute(request, id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "일정 삭제", description = "일정 삭제")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "SESSION_001: 일정을 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "SESSION_002: 이미 취소된 일정입니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<SessionDetailResponse>> cancelSession(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(cancelSessionUsecase.execute(id)));
    }

    @PostMapping("/{sessionId}/qrcodes")
    @Operation(summary = "QR 생성", description = "QR 생성")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "SESSION_001: 일정을 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "SESSION_007: 이미 활성화된 QR 코드가 있습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<QrCodeResponse>> createQrCode(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success(createQrCodeUsecase.excute(sessionId)));
    }
}
