package com.longrunpc.domain.cohort.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.common.entity.BaseEntity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

@Entity
@Table(name = "cohort")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cohort extends BaseEntity {

    @Embedded
    private Generation generation;

    // Cohort는 생성할 일이 없지만 테스트용으로 남겨둠
    @Builder
    private Cohort(Long id, Generation generation, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.generation = Objects.requireNonNull(generation);
    }
}
