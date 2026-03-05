package com.longrunpc.domain.cohort.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Embedded;

import java.util.Objects;

import com.longrunpc.domain.cohort.vo.TeamName;
import com.longrunpc.domain.common.entity.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

@Entity
@Table(name = "team")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private TeamName name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    // Team는 생성할 일이 없지만 테스트용으로 남겨둠
    @Builder
    private Team(Long id, TeamName name, Cohort cohort) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.cohort = Objects.requireNonNull(cohort);
    }
}
