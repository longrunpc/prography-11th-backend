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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final LoginMemberUsecase loginMemberUsecase;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberResponse>> login(@RequestBody LoginMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success(loginMemberUsecase.execute(request)));
    }
}
