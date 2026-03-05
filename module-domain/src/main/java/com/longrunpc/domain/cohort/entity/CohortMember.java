package com.longrunpc.domain.cohort.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @ManyToOne
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;
    
    @ManyToOne
    @JoinColumn(name = "part_id")
    private Part part;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
    
    @Embedded
    private Deposit deposit;

    @Embedded
    private ExcusedCount excusedCount;
    
    @Builder
    private CohortMember(Long id, Member member, Cohort cohort, Part part, Team team, Deposit deposit, ExcusedCount excusedCount) {
        this.id = id;
        this.member = member;
        this.cohort = cohort;
        this.part = part;
        this.team = team;
        this.deposit = deposit;
        this.excusedCount = excusedCount;
    }

    public static CohortMember createCohortMember(Member member, Cohort cohort, Part part, Team team) {
        return CohortMember.builder()
            .member(member)
            .cohort(cohort)
            .part(part)
            .team(team)
            .deposit(new Deposit(0))
            .excusedCount(new ExcusedCount(0))
            .build();
    }
}
