package com.longrunpc.domain.cohort.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.longrunpc.common.exception.BusinessException;

@DisplayName("Deposit 테스트")
public class DepositTest {
    
    @DisplayName("올바른 값으로 생성 시 정상 생성")
    @Test
    void should_create_deposit_when_valid_input() {
        // given
        int value = 100_000;

        // when
        Deposit deposit = new Deposit(value);

        // then
        assertThat(deposit.getValue()).isEqualTo(value);
    }

    @DisplayName("음수 값으로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_negative_input() {
        // given
        int value = -1;
        
        // when & then
        assertThatThrownBy(() -> new Deposit(value))
            .isInstanceOf(BusinessException.class);
    }
}
