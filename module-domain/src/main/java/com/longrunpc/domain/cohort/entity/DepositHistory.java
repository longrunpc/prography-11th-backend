package com.longrunpc.domain.cohort.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import java.time.LocalDateTime;
import java.util.Objects;

import com.longrunpc.common.constant.attendance.AttendanceConstants;
import com.longrunpc.common.constant.cohort.CohortConstants;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.vo.PenaltyAmount;
import com.longrunpc.domain.cohort.vo.Description;
import com.longrunpc.domain.common.entity.BaseEntity;

import lombok.AccessLevel;

@Entity
@Table(name = "deposit_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_member_id", nullable = false)
    private CohortMember cohortMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    @Column(name = "deposit_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DepositType depositType;

    @Column(name = "deposit_amount", nullable = false)
    private int amount;

    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    @Embedded
    private Description description;

    @Builder
    private DepositHistory(Long id, CohortMember cohortMember, Attendance attendance, DepositType depositType, int amount, int balanceAfter, Description description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.cohortMember = Objects.requireNonNull(cohortMember);
        this.attendance = attendance;
        this.depositType = Objects.requireNonNull(depositType);
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public static DepositHistory initialDeposit(CohortMember cohortMember) {
        return DepositHistory.builder()
            .cohortMember(cohortMember)
            .attendance(null)
            .depositType(DepositType.INITIAL)
            .amount(CohortConstants.INITIAL_DEPOSIT)
            .balanceAfter(CohortConstants.INITIAL_DEPOSIT)
            .description(new Description(CohortConstants.INITIAL_DEPOSIT_DESCRIPTION))
            .build();
    }

    public static DepositHistory penaltyDeposit(CohortMember cohortMember, Attendance attendance, PenaltyAmount penaltyAmount) {
        return DepositHistory.builder()
            .cohortMember(cohortMember)
            .attendance(attendance)
            .depositType(DepositType.PENALTY)
            .amount(penaltyAmount.getValue())
            .balanceAfter(cohortMember.getDeposit().getValue() + penaltyAmount.getValue())
            .description(new Description(String.format(AttendanceConstants.REGISTER_PENALTY_DESCRIPTION, attendance.getAttendanceStatus().name(), -1 *penaltyAmount.getValue())))
            .build();
    }

    public static DepositHistory penaltyDepositDiffAmount(CohortMember cohortMember, Attendance attendance, int diff) {
        return DepositHistory.builder()
            .cohortMember(cohortMember)
            .attendance(attendance)
            .depositType(DepositType.PENALTY)
            .amount(diff)
            .balanceAfter(cohortMember.getDeposit().getValue() + diff)
            .description(new Description(String.format(AttendanceConstants.UPDATE_PENALTY_DESCRIPTION, attendance.getAttendanceStatus().name(), -1 *diff)))
            .build();
    }

    public static DepositHistory refundDepositDiffAmount(CohortMember cohortMember, Attendance attendance, int diff) {
        return DepositHistory.builder()
            .cohortMember(cohortMember)
            .attendance(attendance)
            .depositType(DepositType.REFUND)
            .amount(diff)
            .balanceAfter(cohortMember.getDeposit().getValue() + diff)
            .description(new Description(String.format(AttendanceConstants.UPDATE_PENALTY_DESCRIPTION, attendance.getAttendanceStatus().name(), diff)))
            .build();
    }
}
