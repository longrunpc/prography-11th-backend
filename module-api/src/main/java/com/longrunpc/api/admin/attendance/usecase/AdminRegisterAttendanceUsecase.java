package com.longrunpc.api.admin.attendance.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.api.admin.attendance.dto.request.AdminRegisterAttendanceRequest;
import com.longrunpc.api.admin.attendance.dto.response.AdminAttendanceResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.error.AttendanceErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;

@Service
@RequiredArgsConstructor
public class AdminRegisterAttendanceUsecase {

    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final MemberRepository memberRepository;
    private final DepositHistoryRepository depositHistoryRepository;

    @Transactional
    public AdminAttendanceResponse execute(AdminRegisterAttendanceRequest request) {
        // 일정 존재 검증
        Session session = sessionRepository.findById(request.sessionId())
            .orElseThrow(() -> new BusinessException(SessionErrorCode.SESSION_NOT_FOUND));
        // 회원 존재 검증
        Member member = memberRepository.findById(request.memberId())
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
        
        // 중복 출결 확인
        if (attendanceRepository.existsBySessionIdAndMemberId(session.getId(), member.getId())) {
            throw new BusinessException(AttendanceErrorCode.ATTENDANCE_ALREADY_CHECKED);
        }

        // CohortMember 존재 검증
        CohortMember cohortMember = cohortMemberRepository.findByCohortIdAndMemberId(session.getCohort().getId(), member.getId())
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_MEMBER_NOT_FOUND));

        // EXCUSED 등록 시 공결 count 증가
        if (request.status() == AttendanceStatus.EXCUSED) {
            cohortMember.increaseExcusedCount();
        }
        
        // 패널티 계산
        LateMinutes lateMinutes = request.lateMinutes() != null ? new LateMinutes(request.lateMinutes()) : null;
        PenaltyAmount penaltyAmount = Attendance.calculatePenaltyAmount(request.status(), lateMinutes);
        
        Attendance attendance = Attendance.createAttendance(session, null, member, request.status(), lateMinutes, penaltyAmount);
        attendance.changeCheckedInToNull();
        Attendance savedAttendance = attendanceRepository.save(attendance);

        // 패널티가 있을 경우
        if (penaltyAmount.getValue() > 0) {
            cohortMember.decreaseDeposit(penaltyAmount.getValue());

            DepositHistory depositHistory = DepositHistory.penaltyDeposit(cohortMember, savedAttendance, penaltyAmount);
            depositHistoryRepository.save(depositHistory);
        }

        return AdminAttendanceResponse.of(savedAttendance);
    }
}
