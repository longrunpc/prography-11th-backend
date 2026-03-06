package com.longrunpc.domain.attendance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.attendance.vo.AttendanceStatus;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

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

    @Column(name = "reason", nullable = false)
    private Reason reason;

    @Column(name = "checked_in_at", nullable = false)
    private LocalDateTime checkedInAt;
    
    @Builder
    private Attendance(Long id, Session session, CohortMember cohortMember, AttendanceStatus attendanceStatus, LateMinutes lateMinutes, PenaltyAmount penaltyAmount, Reason reason, LocalDateTime checkedInAt) {
        this.id = id;
        this.session = Objects.requireNonNull(session);
        this.cohortMember = Objects.requireNonNull(cohortMember);
        this.attendanceStatus = Objects.requireNonNull(attendanceStatus);
        this.lateMinutes = lateMinutes;
        this.penaltyAmount = penaltyAmount;
        this.reason = reason;
        this.checkedInAt = checkedInAt;
    }

    public static Attendance createAttendance(Session session, CohortMember cohortMember, AttendanceStatus attendanceStatus, LateMinutes lateMinutes, PenaltyAmount penaltyAmount, Reason reason, LocalDateTime checkedInAt) {
        return Attendance.builder()
            .session(session)
            .cohortMember(cohortMember)
            .attendanceStatus(attendanceStatus)
            .lateMinutes(lateMinutes)
            .penaltyAmount(penaltyAmount)
            .reason(reason)
            .checkedInAt(checkedInAt)
            .build();
    }
}
