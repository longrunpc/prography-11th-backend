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
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.common.attendance.AttendanceConstants;
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
    @JoinColumn(name = "cohort_member_id", nullable = false)
    private CohortMember cohortMember;

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
    private Attendance(Long id, Session session, QrCode qrCode, CohortMember cohortMember, AttendanceStatus attendanceStatus, LateMinutes lateMinutes, PenaltyAmount penaltyAmount, Reason reason, LocalDateTime checkedInAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.session = Objects.requireNonNull(session);
        this.qrCode = Objects.requireNonNull(qrCode);
        this.cohortMember = Objects.requireNonNull(cohortMember);
        this.attendanceStatus = Objects.requireNonNull(attendanceStatus);
        this.lateMinutes = lateMinutes;
        this.penaltyAmount = Objects.requireNonNull(penaltyAmount);
        this.reason = reason;
        this.checkedInAt = Objects.requireNonNull(checkedInAt);
    }

    public static Attendance createAttendance(Session session, QrCode qrCode, CohortMember cohortMember, LateMinutes lateMinutes) {
        LocalDateTime checkedInAt = LocalDateTime.now();
        AttendanceStatus attendanceStatus = lateMinutes.getValue() > 0 ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
        PenaltyAmount calculatedPenaltyAmount = calculatePenaltyAmount(attendanceStatus, lateMinutes);
        return Attendance.builder()
            .session(session)
            .qrCode(qrCode)
            .cohortMember(cohortMember)
            .attendanceStatus(attendanceStatus)
            .lateMinutes(lateMinutes)
            .penaltyAmount(calculatedPenaltyAmount)
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
