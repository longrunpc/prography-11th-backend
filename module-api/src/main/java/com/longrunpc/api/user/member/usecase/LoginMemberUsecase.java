package com.longrunpc.api.user.member.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.user.member.dto.request.LoginMemberRequest;
import com.longrunpc.api.user.member.dto.response.MemberResponse;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.member.vo.LoginId;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginMemberUsecase {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public MemberResponse execute(LoginMemberRequest request) {
        // 회원 조회
        Member member = memberRepository.findByLoginId(new LoginId(request.loginId()))
            .orElseThrow(() -> new BusinessException(MemberErrorCode.LOGIN_FAILED));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), member.getPassword().getValue())) {
            throw new BusinessException(MemberErrorCode.LOGIN_FAILED);
        }

        // 탈퇴 회원 확인
        if (member.isWithdrawn()) {
            throw new BusinessException(MemberErrorCode.MEMBER_WITHDRAWN);
        }

        return MemberResponse.of(member);
    }
}
