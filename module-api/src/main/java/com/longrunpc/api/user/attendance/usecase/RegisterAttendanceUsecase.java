package com.longrunpc.api.user.attendance.usecase;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.user.attendance.dto.request.RegisterAttendanceRequest;
import com.longrunpc.api.user.attendance.dto.response.AttendanceDetailResponse;
import com.longrunpc.common.error.AttendanceErrorCode;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.entity.AttendanceStatus;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterAttendanceUsecase {
    private final AttendanceRepository attendanceRepository;
    private final QrCodeRepository qrCodeRepository;
    private final SessionRepository sessionRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final MemberRepository memberRepository;
    private final CohortRepository cohortRepository;
    private final DepositHistoryRepository depositHistoryRepository;
    
    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;
    
    @Transactional
    public AttendanceDetailResponse execute(RegisterAttendanceRequest request) {
        // QR 코드 검증
        QrCode qrCode = qrCodeRepository.findByHashValue(request.hashValue())
            .orElseThrow(() -> new BusinessException(SessionErrorCode.QR_INVALID));
        if (qrCode.isExpired()) {
            throw new BusinessException(SessionErrorCode.QR_EXPIRED);
        }
        
        // 일정 조회
        Session session = sessionRepository.findById(qrCode.getSession().getId())
            .orElseThrow(() -> new BusinessException(SessionErrorCode.SESSION_NOT_FOUND));
        if (!session.isInProgress()) {
            throw new BusinessException(SessionErrorCode.SESSION_NOT_IN_PROGRESS);
        }

        // 회원 조회
        Member member = memberRepository.findById(request.memberId())
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 회원 상태 확인
        if (member.isWithdrawn()) {
            throw new BusinessException(MemberErrorCode.MEMBER_WITHDRAWN);
        }

        // 중복 출결 확인
        if (attendanceRepository.existsBySessionIdAndMemberId(session.getId(), member.getId())) {
            throw new BusinessException(AttendanceErrorCode.ATTENDANCE_ALREADY_CHECKED);
        }

        // 현재 기수의 맴버 존재 확인
        Cohort cohort = cohortRepository.findByGeneration(new Generation(currentGeneration))
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));

        CohortMember cohortMember = cohortMemberRepository.findByCohortIdAndMemberId(cohort.getId(), member.getId())
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_MEMBER_NOT_FOUND));

        // 지각 처리 계산
        LocalDateTime now = LocalDateTime.now();
        LateMinutes lateMinutes = LateMinutes.calculateLateMinutes(now, session.getSessionDate(), session.getSessionTime());
        AttendanceStatus attendanceStatus = lateMinutes != null ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
        
        // 패널티 계산
        PenaltyAmount penaltyAmount = new PenaltyAmount(0);
        if (attendanceStatus == AttendanceStatus.LATE) {
            penaltyAmount = Attendance.calculatePenaltyAmount(attendanceStatus, lateMinutes);
        }

        Attendance attendance = Attendance.createAttendance(session, qrCode, member, attendanceStatus, lateMinutes, penaltyAmount);
        Attendance savedAttendance = attendanceRepository.save(attendance);

        // 패널티가 있을 경우
        if (penaltyAmount.getValue() > 0) {
            // 패널티 납부 처리
            cohortMember.changeDeposit(-1 * penaltyAmount.getValue());

            DepositHistory depositHistory = DepositHistory.penaltyDeposit(cohortMember, savedAttendance, penaltyAmount);
            depositHistoryRepository.save(depositHistory);
        }

        return AttendanceDetailResponse.of(savedAttendance);
    }
}
