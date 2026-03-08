package com.longrunpc.domain.cohort.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;
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

    @Embedded
    private PartName partName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    // Part는 생성할 일이 없지만 테스트용으로 남겨둠
    @Builder
    private Part(Long id, PartName partName, Cohort cohort, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.partName = Objects.requireNonNull(partName);
        this.cohort = Objects.requireNonNull(cohort);
    }
}
