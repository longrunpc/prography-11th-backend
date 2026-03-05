package com.longrunpc.domain.cohort.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.longrunpc.common.exception.BusinessException;

@DisplayName("TeamName 테스트")
public class TeamNameTest {
    @DisplayName("올바른 값으로 생성 시 정상 생성")
    @Test
    void should_create_team_name_when_valid_input() {
        // given
        String value = "test";
        // when
        TeamName teamName = new TeamName(value);
        // then
        assertThat(teamName.getValue()).isEqualTo(value);
    }

    @DisplayName("null 값으로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_null_input() {
        // given
        String value = null;
        // when & then
        assertThatThrownBy(() -> new TeamName(value))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("빈 문자열로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_empty_input() {
        // given
        String value = "";
        // when & then
        assertThatThrownBy(() -> new TeamName(value))
            .isInstanceOf(BusinessException.class);
    }
}
