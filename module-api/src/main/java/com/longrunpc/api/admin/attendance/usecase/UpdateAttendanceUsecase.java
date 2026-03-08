package com.longrunpc.api.admin.attendance.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.attendance.vo.Reason;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.api.admin.attendance.dto.request.UpdateAttendanceRequest;
import com.longrunpc.api.admin.attendance.dto.response.AdminAttendanceResponse;

import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.constant.attendance.AttendanceConstants;
import com.longrunpc.common.error.AttendanceErrorCode;

@Service
@RequiredArgsConstructor
@Transactional  
public class UpdateAttendanceUsecase {
    private final AttendanceRepository attendanceRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final DepositHistoryRepository depositHistoryRepository;

    @Transactional
    public AdminAttendanceResponse execute(UpdateAttendanceRequest request, Long attendanceId) {
        // 출결 존재 검증
        Attendance attendance = attendanceRepository.findById(attendanceId) 
            .orElseThrow(() -> new BusinessException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));

        // CohortMember 존재 검증
        CohortMember cohortMember = cohortMemberRepository.findByCohortIdAndMemberId(attendance.getSession().getCohort().getId(), attendance.getMember().getId())
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_MEMBER_NOT_FOUND));

        // EXCUSED 상태 전환 검증
        if (attendance.getAttendanceStatus() != AttendanceStatus.EXCUSED && request.status() == AttendanceStatus.EXCUSED) {
            cohortMember.increaseExcusedCount();
        }
        if (attendance.getAttendanceStatus() == AttendanceStatus.EXCUSED && request.status() != AttendanceStatus.EXCUSED) {
            cohortMember.decreaseExcusedCount();
        }
            
        // 패널티 계산
        int oldPenaltyAmount = attendance.getPenaltyAmount().getValue();
        int newPenaltyAmount = 0;
        attendance.changeAttendanceStatus(request.status());
        if (request.lateMinutes() != null) {
            LateMinutes newLateMinutes = new LateMinutes(request.lateMinutes());
            newPenaltyAmount = Attendance.calculatePenaltyAmount(request.status(), newLateMinutes).getValue();
            attendance.changeLateMinutes(new LateMinutes(request.lateMinutes()));
        }
        if (request.status() == AttendanceStatus.ABSENT) {
            newPenaltyAmount = AttendanceConstants.MAX_PENALTY_AMOUNT;
        }
        if (request.status() != AttendanceStatus.LATE) {
            attendance.changeLateMinutes(null);
        }
        attendance.changePenaltyAmount(new PenaltyAmount(newPenaltyAmount));

        // 패널티 차액 계산
        int diff = newPenaltyAmount - oldPenaltyAmount;
        if (diff > 0) {
            cohortMember.decreaseDeposit(diff);
            DepositHistory depositHistory = DepositHistory.penaltyDepositDiffAmount(cohortMember, attendance, diff);
            depositHistoryRepository.save(depositHistory);
        }
        if (diff < 0) {
            cohortMember.increaseDeposit(diff * -1);
            DepositHistory depositHistory = DepositHistory.refundDepositDiffAmount(cohortMember, attendance, diff * -1);
            depositHistoryRepository.save(depositHistory);
        }

        if (request.reason() != null) {
            attendance.changeReason(new Reason(request.reason()));
        }
        attendance.changeCheckedInToNull();
        
        return AdminAttendanceResponse.of(attendance);
    }
}
