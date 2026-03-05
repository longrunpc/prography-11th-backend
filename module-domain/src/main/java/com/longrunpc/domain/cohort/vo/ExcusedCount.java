package com.longrunpc.domain.cohort.vo;

import com.longrunpc.common.constant.cohort.CohortConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ExcusedCount {
    
    @Column(name = "excused_count", nullable = false)
    private int value;

    public ExcusedCount(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int value) {
        if (value < CohortConstants.INITIAL_EXCUSED_COUNT) {
            throw new IllegalArgumentException("공결 횟수는 0 이상이어야 합니다.");
        }
        if (value > CohortConstants.MAX_EXCUSED_COUNT) {
            throw new IllegalArgumentException("공결 횟수는 3회를 초과할 수 없습니다.");
        }
    }
}
