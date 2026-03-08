package com.longrunpc.api.user.member.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import com.longrunpc.common.response.ApiResponse;
import com.longrunpc.api.user.member.usecase.ReadMemberUsecase;
import com.longrunpc.api.user.member.dto.response.MemberResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final ReadMemberUsecase readMemberUsecase;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> readMember(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(readMemberUsecase.execute(id)));
    }
}
