package com.longrunpc.domain.attendance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import java.time.LocalDateTime;
import java.util.Objects;

import com.longrunpc.domain.common.entity.BaseEntity;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.common.constant.attendance.AttendanceConstants;
import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.vo.LateMinutes;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.attendance.vo.Reason;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

@Entity
@Table(name = "attendance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qr_code_id", nullable = false)
    private QrCode qrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "attendance_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    @Embedded
    private LateMinutes lateMinutes;
    
    @Embedded
    private PenaltyAmount penaltyAmount;

    @Embedded
    private Reason reason;

    @Column(name = "checked_in_at", nullable = false)
    private LocalDateTime checkedInAt;
    
    @Builder
    private Attendance(Long id, Session session, QrCode qrCode, Member member, AttendanceStatus attendanceStatus, LateMinutes lateMinutes, PenaltyAmount penaltyAmount, Reason reason, LocalDateTime checkedInAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.session = Objects.requireNonNull(session);
        this.qrCode = qrCode;
        this.member = Objects.requireNonNull(member);
        this.attendanceStatus = Objects.requireNonNull(attendanceStatus);
        this.lateMinutes = lateMinutes;
        this.penaltyAmount = Objects.requireNonNull(penaltyAmount);
        this.reason = reason;
        this.checkedInAt = Objects.requireNonNull(checkedInAt);
    }

    public static Attendance createAttendance(Session session, QrCode qrCode, Member member, AttendanceStatus attendanceStatus, LateMinutes lateMinutes, PenaltyAmount penaltyAmount) {
        LocalDateTime checkedInAt = LocalDateTime.now();
        return Attendance.builder()
            .session(session)
            .qrCode(qrCode)
            .member(member)
            .attendanceStatus(attendanceStatus)
            .lateMinutes(lateMinutes)
            .penaltyAmount(penaltyAmount)
            .reason(null)
            .checkedInAt(checkedInAt)
            .build();
    }

    public static PenaltyAmount calculatePenaltyAmount(AttendanceStatus attendanceStatus, LateMinutes lateMinutes) {
        if (attendanceStatus == AttendanceStatus.LATE) {
            return new PenaltyAmount(Math.min(AttendanceConstants.MAX_PENALTY_AMOUNT, AttendanceConstants.LATE_MINUTES_PENALTY_AMOUNT * lateMinutes.getValue()));
        }
        if (attendanceStatus == AttendanceStatus.ABSENT) {
            return new PenaltyAmount(AttendanceConstants.MAX_PENALTY_AMOUNT);
        }
        if (attendanceStatus == AttendanceStatus.EXCUSED) {
            return new PenaltyAmount(0);
        }
        return new PenaltyAmount(0);
    }

    public void changeAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = Objects.requireNonNull(attendanceStatus);
    }

    public void changeLateMinutes(LateMinutes lateMinutes) {
        if(lateMinutes.getValue() < 0) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT);
        }
        this.lateMinutes = Objects.requireNonNull(lateMinutes);
    }

    public void changePenaltyAmount(PenaltyAmount penaltyAmount) {
        this.penaltyAmount = Objects.requireNonNull(penaltyAmount);
    }

    public void changeReason(Reason reason) {
        this.reason = reason;
    }
}
