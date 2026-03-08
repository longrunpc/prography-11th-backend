package com.longrunpc.api.admin.session.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.api.admin.session.dto.response.QrCodeResponse;
import com.longrunpc.common.constant.qrCode.QrCodeConstants;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.session.vo.QrCodeHashValue;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.entity.SessionStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DisplayName("CreateQrCodeUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class CreateQrCodeUsecaseTest {
    
    @InjectMocks
    private CreateQrCodeUsecase createQrCodeUsecase;
    @Mock
    private QrCodeRepository qrCodeRepository;
    @Mock
    private SessionRepository sessionRepository;

    private Session session;
    private Cohort cohort;
    private QrCode qrCode;
    private LocalDateTime now;
    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(11))
            .cohortName(new CohortName("11기"))
            .build();
        session = Session.builder()
            .id(1L)
            .cohort(cohort)
            .title(new SessionTitle("test"))
            .sessionDate(now.toLocalDate().plusDays(1))
            .sessionTime(now.toLocalTime().plusHours(1))
            .sessionLocation(new SessionLocation("test"))
            .sessionStatus(SessionStatus.SCHEDULED)
            .build();
        qrCode = QrCode.builder()
            .id(1L)
            .session(session)
            .hashValue(new QrCodeHashValue("hashValue"))
            .expiresAt(now.plusHours(QrCodeConstants.EXPIRATION_HOURS))
            .build();
    }

    @DisplayName("QR 코드 생성 성공")
    @Test
    void should_create_qr_code_when_valid_input() {
        // given
        given(sessionRepository.findById(session.getId())).willReturn(Optional.of(session));
        given(qrCodeRepository.findBySessionIdAndExpiresAtAfter(
            eq(session.getId()), 
            any(LocalDateTime.class)
        )).willReturn(List.of());
        given(qrCodeRepository.save(any(QrCode.class))).willReturn(qrCode);

        // when
        QrCodeResponse response = createQrCodeUsecase.excute(session.getId());

        // then
        assertThat(response.id()).isEqualTo(qrCode.getId());
        assertThat(response.sessionId()).isEqualTo(session.getId());
        assertThat(response.hashValue()).isEqualTo(qrCode.getHashValue().getValue());
        assertThat(response.createdAt()).isEqualTo(qrCode.getCreatedAt());
        assertThat(response.expiresAt()).isEqualTo(qrCode.getExpiresAt());
    }

    @DisplayName("QR 코드 생성 실패 - 세션 존재하지 않음")
    @Test
    void should_fail_create_qr_code_when_session_not_found() {
        // given
        given(sessionRepository.findById(session.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> createQrCodeUsecase.excute(session.getId()))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.SESSION_NOT_FOUND.getMessage());
    }

    @DisplayName("QR 코드 생성 실패 - QR 코드 이미 활성화됨")
    @Test
    void should_fail_create_qr_code_when_qr_code_already_active() {
        // given
        given(sessionRepository.findById(session.getId())).willReturn(Optional.of(session));
        given(qrCodeRepository.findBySessionIdAndExpiresAtAfter(
            eq(session.getId()), 
            any(LocalDateTime.class)
        )).willReturn(List.of(qrCode));

        // when & then
        assertThatThrownBy(() -> createQrCodeUsecase.excute(session.getId()))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.QR_ALREADY_ACTIVE.getMessage());
    }
}
