package com.longrunpc.api.user.session.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.longrunpc.api.user.session.dto.response.SessionResponse;
import com.longrunpc.api.user.session.usecase.ReadValidSessionsUsecase;
import com.longrunpc.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class SessionController {

    private final ReadValidSessionsUsecase readValidSessionsUsecase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SessionResponse>>> readValidSessions() {
        return ResponseEntity.ok(ApiResponse.success(readValidSessionsUsecase.execute()));
    }
}
