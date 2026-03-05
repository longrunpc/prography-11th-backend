package com.longrunpc.domain.session.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.longrunpc.common.exception.BusinessException;

@DisplayName("QrCodeHashValue 테스트")
public class QrCodeHashValueTest {

    @DisplayName("올바른 값으로 생성 시 정상 생성")
    @Test
    void should_create_qr_code_hash_value_when_valid_input() {
        // given
        String value = "test";

        // when
        QrCodeHashValue qrCodeHashValue = new QrCodeHashValue(value);

        // then
        assertThat(qrCodeHashValue.getValue()).isEqualTo(value);
    }

    @DisplayName("null 값으로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_null_input() {
        // given
        String value = null;

        // when & then
        assertThatThrownBy(() -> new QrCodeHashValue(value))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("빈 문자열로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_empty_input() {
        // given
        String value = "";

        // when & then
        assertThatThrownBy(() -> new QrCodeHashValue(value))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("generate 메서드 테스트")
    @Test
    void should_generate_qr_code_hash_value_when_valid_input() {
        // when
        String value = QrCodeHashValue.generate();

        // then
        assertThat(value).isNotNull();
        assertThat(value.length()).isEqualTo(36);
        assertThat(value.split("-").length).isEqualTo(5);
    }
}
