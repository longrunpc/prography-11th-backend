package com.longrunpc.domain.cohort.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.Objects;

import com.longrunpc.common.constant.cohort.CohortConstants;
import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.ExcusedCount;
import com.longrunpc.domain.common.entity.BaseEntity;
import com.longrunpc.domain.member.entity.Member;

@Entity
@Table(name = "cohort_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CohortMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    
    @Embedded
    private Deposit deposit;

    @Embedded
    private ExcusedCount excusedCount;
    
    @Builder
    private CohortMember(Long id, Member member, Cohort cohort, Part part, Team team, Deposit deposit, ExcusedCount excusedCount) {
        this.id = id;
        this.member = Objects.requireNonNull(member);
        this.cohort = Objects.requireNonNull(cohort);
        this.part = part;
        this.team = team;
        this.deposit = Objects.requireNonNull(deposit);
        this.excusedCount = Objects.requireNonNull(excusedCount);
    }

    public static CohortMember createCohortMember(Member member, Cohort cohort, Part part, Team team) {
        return CohortMember.builder()
            .member(member)
            .cohort(cohort)
            .part(part)
            .team(team)
            .deposit(new Deposit(CohortConstants.INITIAL_DEPOSIT))
            .excusedCount(new ExcusedCount(CohortConstants.INITIAL_EXCUSED_COUNT))
            .build();
    }

    public boolean isDepositEnough(int amount) {
        return this.deposit.getValue() >= amount;
    }

    public void increaseDeposit(int amount) {
        if (amount < 0) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT);
        }
        this.deposit = new Deposit(this.deposit.getValue() + amount);
    }

    public void decreaseDeposit(int amount) {
        if (amount < 0) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT);
        }
        this.deposit = new Deposit(this.deposit.getValue() - amount);
    }

    public void increaseExcusedCount() {
        this.excusedCount = new ExcusedCount(this.excusedCount.getValue() + 1);
    }
}
