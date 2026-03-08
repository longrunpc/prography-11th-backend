package com.longrunpc.api.admin.session.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import com.longrunpc.common.response.ApiResponse;
import com.longrunpc.api.admin.session.usecase.ReissueQrCodeUsecase;
import com.longrunpc.api.admin.session.dto.response.QrCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/qrcodes")
@Tag(name = "Admin QR Code", description = "관리자 QR 코드 API")
public class AdminQrCodeController {

    private final ReissueQrCodeUsecase reissueQrCodeUsecase;

    @PutMapping("/{qrCodeId}")
    @Operation(summary = "QR 갱신", description = "QR 갱신")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "SESSION_004: QR 코드를 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<QrCodeResponse>> reissueQrCode(@PathVariable Long qrCodeId) {
        return ResponseEntity.ok(ApiResponse.success(reissueQrCodeUsecase.execute(qrCodeId)));
    }
}
