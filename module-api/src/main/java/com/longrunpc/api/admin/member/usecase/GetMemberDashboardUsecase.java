package com.longrunpc.api.admin.member.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.member.dto.response.MemberDashboardResponse;
import com.longrunpc.api.admin.member.dto.request.MemberDashboardRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetMemberDashboardUsecase {
    
    @Transactional(readOnly = true)
    public MemberDashboardResponse execute(MemberDashboardRequest request) {
        return null;
    }
}
