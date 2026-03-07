package com.longrunpc.api.admin.member.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.member.dto.request.RegisterMemberRequest;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.domain.cohort.repository.PartRepository;
import com.longrunpc.domain.cohort.repository.TeamRepository;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.cohort.entity.Part;
import com.longrunpc.domain.cohort.entity.Team;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterMemberUsecase {

    private final MemberRepository memberRepository;
    private final CohortMemberRepository cohortMemberRepository;   
    private final DepositHistoryRepository depositHistoryRepository;
    private final CohortRepository cohortRepository;
    private final PartRepository partRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public void execute(RegisterMemberRequest request) {
        // 아이디 중복 검사
        if (memberRepository.findByLoginId(new LoginId(request.loginId())).isPresent()) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_LOGIN_ID);
        }

        // 기수, 파트, 팀 존재 검증
        Cohort cohort = cohortRepository.findById(request.cohortId())
                            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));
        Part part = request.partId() == null ? null : 
            partRepository.findById(request.partId())
                .orElseThrow(() -> new BusinessException(CohortErrorCode.PART_NOT_FOUND));
        Team team = request.teamId() == null ? null : 
            teamRepository.findById(request.teamId())
                .orElseThrow(() -> new BusinessException(CohortErrorCode.TEAM_NOT_FOUND));
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());
        
        // member 생성
        Member member = Member.createMember(
            new LoginId(request.loginId()),
            new Password(encodedPassword),
            new MemberName(request.memberName()),
            new Phone(request.phone())
        );
        memberRepository.save(member);

        // CohortMember 생성
        CohortMember cohortMember = CohortMember.createCohortMember(member, cohort, part, team);
        cohortMemberRepository.save(cohortMember);

        // DepositHistory 생성
        DepositHistory depositHistory = DepositHistory.initialDeposit(cohortMember);
        depositHistoryRepository.save(depositHistory);
    }
}
