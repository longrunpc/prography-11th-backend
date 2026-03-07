package com.longrunpc.domain.cohort.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.longrunpc.common.exception.BusinessException;

@DisplayName("CohortName 테스트")
public class CohortNameTest {
    @DisplayName("올바른 값으로 생성 시 정상 생성")
    @Test
    void should_create_cohort_name_when_valid_input() {
        // given
        String value = "test";
        // when
        CohortName cohortName = new CohortName(value);
        // then
        assertThat(cohortName.getValue()).isEqualTo(value);
    }

    @DisplayName("null 값으로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_null_input() {
        // given
        String value = null;
        // when & then
        assertThatThrownBy(() -> new CohortName(value))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("빈 문자열로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_empty_input() {
        // given
        String value = "";
        // when & then
        assertThatThrownBy(() -> new CohortName(value))
            .isInstanceOf(BusinessException.class);
    }
}
