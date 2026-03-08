package com.longrunpc.api.admin.member.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.api.admin.member.dto.response.MemberDetailResponse;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadMemberDetailUsecase {

    private final CohortMemberRepository cohortMemberRepository;

    @Transactional(readOnly = true)
    public MemberDetailResponse execute(Long memberId) {
        CohortMember cohortMember = cohortMemberRepository.findDetailByMemberId(memberId)
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        return MemberDetailResponse.of(cohortMember);
    }
}
