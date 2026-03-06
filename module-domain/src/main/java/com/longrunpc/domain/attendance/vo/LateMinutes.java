package com.longrunpc.domain.attendance.vo;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.common.error.GlobalErrorCode;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class LateMinutes {

    @Column(name = "late_minutes", nullable = false)
    private int value;

    public LateMinutes(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int value) {
        if (value < 0) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT);
        }
    }
}
