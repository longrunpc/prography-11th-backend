package com.longrunpc.domain.attendance.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.longrunpc.common.exception.BusinessException;

@DisplayName("PenaltyAmount 테스트")
public class PenaltyAmountTest {

    @DisplayName("올바른 값으로 생성 시 정상 생성")
    @Test
    void should_create_penalty_amount_when_valid_input() {
        // given
        int value = 3_000;

        // when
        PenaltyAmount penaltyAmount = new PenaltyAmount(value);

        // then
        assertThat(penaltyAmount.getValue()).isEqualTo(value);
    }

    @DisplayName("음수 값으로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_negative_input() {
        // given
        int value = -1;

        // when & then
        assertThatThrownBy(() -> new PenaltyAmount(value))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("벌금 차액 계산 시 신규 벌금 - 기존 벌금 반환")
    @Test
    void should_return_penalty_amount_diff() {
        // given
        PenaltyAmount oldPenaltyAmount = new PenaltyAmount(2_000);
        PenaltyAmount newPenaltyAmount = new PenaltyAmount(7_000);

        // when
        int diff = PenaltyAmount.penaltyAmountDiff(oldPenaltyAmount, newPenaltyAmount);

        // then
        assertThat(diff).isEqualTo(5_000);
    }
}
