package com.longrunpc.domain.session.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.longrunpc.common.constant.qrCode.QrCodeConstants;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;

@DisplayName("QrCode 엔티티 테스트")
public class QrCodeTest {
    private Session session;
    private Cohort cohort;

    @BeforeEach
    void setUp() {
        // given
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(11))
            .cohortName(new CohortName("11기"))
            .build();
        session = Session.builder()
            .id(1L)
            .cohort(cohort)
            .title(new SessionTitle("test"))
            .sessionDate(LocalDate.now())
            .sessionTime(LocalTime.now())
            .sessionLocation(new SessionLocation("test"))
            .sessionStatus(SessionStatus.SCHEDULED)
            .build();
    }

    @DisplayName("createQrCode 메서드 테스트")
    @Nested
    class CreateQrCodeTest {
        @DisplayName("유효한 입력 시 정상 생성")
        @Test
        void should_create_qr_code_when_valid_input() {
            // when
            QrCode qrCode = QrCode.createQrCode(session);

            // then
            assertThat(qrCode.getSession()).isEqualTo(session);
            assertThat(qrCode.getHashValue()).isNotNull();
            assertThat(qrCode.getCreatedAt()).isAfter(LocalDateTime.now().minusSeconds(1));
            assertThat(qrCode.getExpiresAt()).isEqualTo(qrCode.getCreatedAt().plusHours(QrCodeConstants.EXPIRATION_HOURS));
        }

        @DisplayName("session 필드 null 시 예외 발생")
        @Test
        void should_throw_exception_when_invalid_input() {
            // given
            Session session = null;
            
            // when & then
            assertThatThrownBy(() -> QrCode.createQrCode(session))
                .isInstanceOf(BusinessException.class);
        }
    }

    @DisplayName("expire 메서드 테스트")   
    @Nested
    class ExpireTest {
        @DisplayName("만료 시 정상 작동")
        @Test
        void should_expire_when_valid_input() {
            // given
            QrCode qrCode = QrCode.createQrCode(session);
            // when
            qrCode.expire();
            // then
            assertThat(qrCode.getExpiresAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        }
    }

    @DisplayName("isExpired 메서드 테스트")
    @Nested
    class IsExpiredTest {
        @DisplayName("만료 시 true 반환")
        @Test
        void should_return_true_when_expired() {
            // given
            QrCode qrCode = QrCode.createQrCode(session);
            qrCode.expire();

            // when & then
            assertThat(qrCode.isExpired()).isTrue();
        }
        @DisplayName("만료 아닐 시 false 반환")
        @Test
        void should_return_false_when_not_expired() {
            // given
            QrCode qrCode = QrCode.createQrCode(session);

            // when & then
            assertThat(qrCode.isExpired()).isFalse();
        }
    }
}