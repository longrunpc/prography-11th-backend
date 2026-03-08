package com.longrunpc.api.admin.attendance.usecase;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.attendance.dto.response.MemberAttendanceSummaryResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.domain.cohort.vo.Generation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadAttendanceSummaryUsecase {
    private final AttendanceRepository attendanceRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final DepositHistoryRepository depositHistoryRepository;
    private final CohortRepository cohortRepository;

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    @Transactional
    public List<MemberAttendanceSummaryResponse> execute(Long sessionId) {
        Cohort cohort = cohortRepository.findByGeneration(new Generation(currentGeneration))
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));

        List<CohortMember> cohortMembers = cohortMemberRepository.findAllByCohortIdWithMember(cohort.getId());

        List<Long> memberIds = cohortMembers.stream()
            .map(cm -> cm.getMember().getId())
            .collect(Collectors.toList());

        Map<Long, List<Attendance>> attendanceMap = attendanceRepository.findAllByMemberIdIn(memberIds)
            .stream()
            .collect(Collectors.groupingBy(a -> a.getMember().getId()));
        
        return cohortMembers.stream()
        .map(cm -> {
            List<Attendance> memberAttendances = attendanceMap.getOrDefault(cm.getMember().getId(), List.of());
            
            return MemberAttendanceSummaryResponse.of(
                cm,                             
                countStatus(memberAttendances),  
                calculateTotalPenalty(memberAttendances) 
            );
        })
        .collect(Collectors.toList());
    }

    private Map<AttendanceStatus, Long> countStatus(List<Attendance> attendances) {
        return attendances.stream()
            .collect(Collectors.groupingBy(
                Attendance::getAttendanceStatus, 
                Collectors.counting()
            ));
    }

    private int calculateTotalPenalty(List<Attendance> attendances) {
        return attendances.stream()
            .mapToInt(a -> a.getPenaltyAmount().getValue())
            .sum();
    }
}
