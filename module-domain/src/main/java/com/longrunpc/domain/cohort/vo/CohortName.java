package com.longrunpc.domain.cohort.vo;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.exception.BusinessException;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class CohortName {

    @Column(name = "cohort_name", nullable = false)
    private String value;

    public CohortName(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT);
        }
    }
}
