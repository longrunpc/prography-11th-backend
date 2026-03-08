package com.longrunpc.domain.cohort.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.longrunpc.common.constant.cohort.CohortConstants;
import com.longrunpc.common.exception.BusinessException;

@DisplayName("ExcusedCount 테스트")
public class ExcusedCountTest {

    @DisplayName("올바른 값으로 생성 시 정상 생성")
    @Test
    void should_create_excused_count_when_valid_input() {
        // given
        int value = 1;

        // when
        ExcusedCount excusedCount = new ExcusedCount(value);

        // then
        assertThat(excusedCount.getValue()).isEqualTo(value);
    }

    @DisplayName("음수 값으로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_negative_input() {
        // given
        int value = -1;

        // when & then
        assertThatThrownBy(() -> new ExcusedCount(value))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("초과 값으로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_exceed_input() {
        // given
        int value = CohortConstants.MAX_EXCUSED_COUNT + 1;
        
        // when & then
        assertThatThrownBy(() -> new ExcusedCount(value))
            .isInstanceOf(BusinessException.class);
    }
}
