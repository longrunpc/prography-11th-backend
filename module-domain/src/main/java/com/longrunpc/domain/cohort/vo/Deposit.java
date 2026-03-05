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
public class Deposit {

    @Column(name = "deposit", nullable = false)
    private int value;

    public Deposit(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int value) {
        if (value < CohortConstants.INITIAL_DEPOSIT) {
            throw new IllegalArgumentException("Deposit는 0 이상이어야 합니다.");
        }
    }
}
