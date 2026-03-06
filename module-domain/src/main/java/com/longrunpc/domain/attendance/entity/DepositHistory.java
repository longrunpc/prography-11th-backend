package com.longrunpc.domain.attendance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Builder;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.common.entity.BaseEntity;

@Entity
@Table(name = "deposit_history")
public class DepositHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_member_id", nullable = false)
    private CohortMember cohortMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    @Column(name = "deposit_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DepositType depositType;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    @Column(name = "description", nullable = false)
    private String description;

    @Builder
    private DepositHistory(CohortMember cohortMember, Attendance attendance, DepositType depositType, int amount, int balanceAfter, String description) {
        this.cohortMember = cohortMember;
        this.attendance = attendance;
        this.depositType = depositType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public static DepositHistory createDepositHistory(CohortMember cohortMember, Attendance attendance, DepositType depositType, int amount, int balanceAfter, String description) {
        return DepositHistory.builder()
            .cohortMember(cohortMember)
            .attendance(attendance)
            .depositType(depositType)
            .amount(amount)
            .balanceAfter(balanceAfter)
            .description(description)
            .build();
    }
}
