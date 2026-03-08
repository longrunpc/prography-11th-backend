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
public class PartName {
    
    @Column(name = "part_name", nullable = false)
    private String value;

    public PartName(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT);
        }
    }
}
