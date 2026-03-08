package com.longrunpc.domain.attendance.vo;

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
public class PenaltyAmount {

    @Column(name = "penalty_amount", nullable = false)
    private int value;

    public PenaltyAmount(int value) {
        this.value = value;
    }

    public static int penaltyAmountDiff(PenaltyAmount oldPenaltyAmount, PenaltyAmount newPenaltyAmount) {
        return newPenaltyAmount.getValue() - oldPenaltyAmount.getValue();
    }
}
