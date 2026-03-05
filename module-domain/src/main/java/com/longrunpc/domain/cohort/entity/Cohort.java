package com.longrunpc.domain.cohort.entity;

import java.util.Objects;

import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.common.entity.BaseEntity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Generation generation;

    // Cohort는 생성할 일이 없지만 테스트용으로 남겨둠
    @Builder
    private Cohort(Long id, Generation generation) {
        this.id = id;
        this.generation = Objects.requireNonNull(generation);
    }
}
