package com.longrunpc.domain.member.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.longrunpc.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
    

@DisplayName("LoginId 테스트")
public class LoginIdTest {

    @DisplayName("올바른 값으로 생성 시 정상 생성")
    @Test
    void should_create_login_id_when_valid_input() {
        // given
        String value = "test@example.com";

        // when
        LoginId loginId = new LoginId(value);

        // then
        assertThat(loginId.getValue()).isEqualTo(value);
    }

    @DisplayName("null 값으로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_null_input() {
        // given
        String value = null;

        // when & then
        assertThatThrownBy(() -> new LoginId(value))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("빈 문자열로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_empty_input() {
        // given
        String value = "";

        // when & then
        assertThatThrownBy(() -> new LoginId(value))
            .isInstanceOf(BusinessException.class);
    }
}
