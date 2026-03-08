package com.longrunpc.api.admin.attendance.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.attendance.dto.response.MemberAttendanceResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.cohort.entity.CohortMember;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadMemberAttendanceDetailUsecase {
    private final AttendanceRepository attendanceRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final MemberRepository memberRepository;
    
    @Transactional(readOnly = true)
    public MemberAttendanceResponse execute(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
        CohortMember cohortMember = cohortMemberRepository.findDetailByMemberId(member.getId())
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_MEMBER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepository.findAllByMemberId(member.getId());
        return MemberAttendanceResponse.of(cohortMember, attendances);
    }

}
