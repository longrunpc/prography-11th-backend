package com.longrunpc.api.user.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.longrunpc.api.user.member.dto.request.LoginMemberRequest;
import com.longrunpc.api.user.member.dto.response.MemberResponse;
import com.longrunpc.api.user.member.usecase.LoginMemberUsecase;
import com.longrunpc.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final LoginMemberUsecase loginMemberUsecase;
    
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "회원 로그인")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "COMMON_001: 입력값이 올바르지 않습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "MEMBER_001: 로그인 아이디 또는 비밀번호가 올바르지 않습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "MEMBER_002: 탈퇴한 회원입니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "COMMON_002: 서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<ApiResponse<MemberResponse>> login(@Valid @RequestBody LoginMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success(loginMemberUsecase.execute(request)));
    }
}
