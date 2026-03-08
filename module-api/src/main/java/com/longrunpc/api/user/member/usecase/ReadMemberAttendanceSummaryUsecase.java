package com.longrunpc.api.user.member.usecase;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.user.member.dto.response.MemberAttendanceSummaryResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadMemberAttendanceSummaryUsecase {
    private final AttendanceRepository attendanceRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final CohortRepository cohortRepository;
    private final MemberRepository memberRepository;

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    @Transactional(readOnly = true)
    public MemberAttendanceSummaryResponse execute(Long memberId) {
        Cohort cohort = cohortRepository.findByGeneration(new Generation(currentGeneration))
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
        CohortMember cohortMember = cohortMemberRepository.findByCohortIdAndMemberId(cohort.getId(), member.getId())
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_MEMBER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepository.findAllByMemberId(member.getId());
        Map<AttendanceStatus, Long> attendanceStatusMap = attendances.stream()
            .collect(Collectors.groupingBy(Attendance::getAttendanceStatus, Collectors.counting()));

        int totalPenalty = attendances.stream()
            .mapToInt(a -> a.getPenaltyAmount().getValue())
            .sum();

        return MemberAttendanceSummaryResponse.of(cohortMember, attendanceStatusMap, totalPenalty);
    }
}
