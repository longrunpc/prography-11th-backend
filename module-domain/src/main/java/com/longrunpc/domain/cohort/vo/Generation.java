package com.longrunpc.domain.cohort.vo;

import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.exception.BusinessException;

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
public class Generation {

    @Column(name = "generation", nullable = false)
    private int value;

    public Generation(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int value) {
        if (value < 0) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT);
        }
    }
}
