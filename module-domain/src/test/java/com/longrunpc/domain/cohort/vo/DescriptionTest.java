package com.longrunpc.domain.cohort.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.longrunpc.common.exception.BusinessException;

@DisplayName("Description 테스트")
public class DescriptionTest {

    @DisplayName("올바른 값으로 생성 시 정상 생성")
    @Test
    void should_create_description_when_valid_input() {
        // given
        String value = "보증금 차감";

        // when
        Description description = new Description(value);

        // then
        assertThat(description.getValue()).isEqualTo(value);
    }

    @DisplayName("null 값으로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_null_input() {
        // given
        String value = null;

        // when & then
        assertThatThrownBy(() -> new Description(value))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("빈 문자열로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_empty_input() {
        // given
        String value = "";

        // when & then
        assertThatThrownBy(() -> new Description(value))
            .isInstanceOf(BusinessException.class);
    }
}
