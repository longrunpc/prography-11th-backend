package com.longrunpc.api.admin.session.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import com.longrunpc.common.response.ApiResponse;
import com.longrunpc.api.admin.session.usecase.ReissueQrCodeUsecase;
import com.longrunpc.api.admin.session.dto.response.QrCodeResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/qrcodes")
public class AdminQrCodeController {

    private final ReissueQrCodeUsecase reissueQrCodeUsecase;

    @PostMapping("/{qrCodeId}")
    public ResponseEntity<ApiResponse<QrCodeResponse>> reissueQrCode(@PathVariable Long qrCodeId) {
        return ResponseEntity.ok(ApiResponse.success(reissueQrCodeUsecase.execute(qrCodeId)));
    }
}
