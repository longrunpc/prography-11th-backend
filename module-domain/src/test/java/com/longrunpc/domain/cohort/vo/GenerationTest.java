package com.longrunpc.domain.cohort.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.longrunpc.common.exception.BusinessException;

@DisplayName("Generation 테스트")  
public class GenerationTest {

    @DisplayName("올바른 값으로 생성 시 정상 생성")
    @Test
    void should_create_generation_when_valid_input() {
        // given
        int value = 11;
        // when
        Generation generation = new Generation(value);
        // then
        assertThat(generation.getValue()).isEqualTo(value);
    }

    @DisplayName("음수 값으로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_negative_input() {
        // given
        int value = -1;
        // when & then
        assertThatThrownBy(() -> new Generation(value))
            .isInstanceOf(BusinessException.class);
    }
}
