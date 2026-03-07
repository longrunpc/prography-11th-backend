package com.longrunpc.api.admin.member.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.member.dto.request.UpdateMemberRequest;
import com.longrunpc.api.admin.member.dto.response.MemberDetailResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.entity.Part;
import com.longrunpc.domain.cohort.entity.Team;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.PartRepository;
import com.longrunpc.domain.cohort.repository.TeamRepository;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateMemberUsecase {

    private final CohortRepository cohortRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final PartRepository partRepository;
    private final TeamRepository teamRepository;
    
    @Transactional
    public MemberDetailResponse execute(UpdateMemberRequest request, Long memberId) {
        CohortMember cohortMember = cohortMemberRepository.findDetailByMemberId(memberId)
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
        Member member = cohortMember.getMember();

        if (request.name() != null) {
            member.changeMemberName(new MemberName(request.name()));
        }
        if (request.phone() != null) {
            member.changePhone(new Phone(request.phone()));
        }
        if (request.cohortId() != null) {
            Cohort cohort = cohortRepository.findById(request.cohortId())
                .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));
            cohortMember.changeCohort(cohort);
        }
        if (request.partId() != null) {
            Part part = partRepository.findById(request.partId())
                .orElseThrow(() -> new BusinessException(CohortErrorCode.PART_NOT_FOUND));
            cohortMember.changePart(part);
        }
        if (request.teamId() != null) {
            Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new BusinessException(CohortErrorCode.TEAM_NOT_FOUND));
            cohortMember.changeTeam(team);
        }

        return MemberDetailResponse.of(cohortMember);
    }
}
