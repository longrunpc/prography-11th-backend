package com.longrunpc.domain.attendance.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.longrunpc.common.exception.BusinessException;

@DisplayName("LateMinutes 테스트")
public class LateMinutesTest {

    @DisplayName("올바른 값으로 생성 시 정상 생성")
    @Test
    void should_create_late_minutes_when_valid_input() {
        // given
        int value = 10;

        // when
        LateMinutes lateMinutes = new LateMinutes(value);

        // then
        assertThat(lateMinutes.getValue()).isEqualTo(value);
    }

    @DisplayName("음수 값으로 생성 시 예외 발생")
    @Test
    void should_throw_exception_when_negative_input() {
        // given
        int value = -1;

        // when & then
        assertThatThrownBy(() -> new LateMinutes(value))
            .isInstanceOf(BusinessException.class);
    }

    @DisplayName("출석 시각이 세션 시각보다 빠르면 0분 반환")
    @Test
    void should_return_zero_when_checked_in_before_session_time() {
        // given
        LocalDate sessionDate = LocalDate.of(2026, 3, 6);
        LocalTime sessionTime = LocalTime.of(19, 0);
        LocalDateTime checkedInAt = LocalDateTime.of(2026, 3, 6, 18, 59);

        // when
        LateMinutes lateMinutes = LateMinutes.calculateLateMinutes(checkedInAt, sessionDate, sessionTime);

        // then
        assertThat(lateMinutes).isNull();
    }

    @DisplayName("출석 시각이 세션 시각보다 늦으면 지각분 반환")
    @Test
    void should_return_late_minutes_when_checked_in_after_session_time() {
        // given
        LocalDate sessionDate = LocalDate.of(2026, 3, 6);
        LocalTime sessionTime = LocalTime.of(19, 0);
        LocalDateTime checkedInAt = LocalDateTime.of(2026, 3, 6, 19, 7);

        // when
        LateMinutes lateMinutes = LateMinutes.calculateLateMinutes(checkedInAt, sessionDate, sessionTime);

        // then
        assertThat(lateMinutes).isEqualTo(new LateMinutes(7));
    }
}
