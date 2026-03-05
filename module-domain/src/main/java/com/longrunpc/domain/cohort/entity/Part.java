package com.longrunpc.domain.cohort.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

import com.longrunpc.domain.cohort.vo.PartName;
import com.longrunpc.domain.common.entity.BaseEntity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

@Entity
@Table(name = "part")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Part extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PartName name;

    @ManyToOne
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    @Builder
    private Part(Long id, PartName name, Cohort cohort) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.cohort = Objects.requireNonNull(cohort);
    }
}
