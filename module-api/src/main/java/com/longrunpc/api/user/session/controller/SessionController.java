package com.longrunpc.api.user.session.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.longrunpc.api.user.session.dto.response.SessionResponse;
import com.longrunpc.api.user.session.usecase.ReadValidSessionsUsecase;
import com.longrunpc.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions")
@Tag(name = "Session", description = "사용자 세션 API")
public class SessionController {

    private final ReadValidSessionsUsecase readValidSessionsUsecase;

    @GetMapping
    @Operation(summary = "일정 목록 (회원)", description = "일정 목록 (회원)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "COHORT_001: 기수를 찾을 수 없습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<List<SessionResponse>>> readValidSessions() {
        return ResponseEntity.ok(ApiResponse.success(readValidSessionsUsecase.execute()));
    }
}
