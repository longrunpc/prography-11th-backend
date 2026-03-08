package com.longrunpc.api.admin.session.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.api.admin.session.dto.response.QrCodeResponse;
import com.longrunpc.common.constant.qrCode.QrCodeConstants;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.session.vo.SessionTitle;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.QrCodeHashValue;
import com.longrunpc.domain.session.entity.SessionStatus;
import java.time.LocalDateTime;
import java.util.Optional;

@DisplayName("ReissueQrCodeUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class ReissueQrCodeUsecaseTest {
    @InjectMocks
    private ReissueQrCodeUsecase reissueQrCodeUsecase;
    @Mock
    private QrCodeRepository qrCodeRepository;

    private QrCode qrCode;
    private QrCode newQrCode;
    private LocalDateTime now;
    private Session session;
    private Cohort cohort;
    
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
        newQrCode = QrCode.builder()
            .id(2L)
            .session(session)
            .hashValue(new QrCodeHashValue("hashValue"))
            .expiresAt(now.plusHours(QrCodeConstants.EXPIRATION_HOURS))
            .build();
    }

    @DisplayName("QR 코드 재발급 성공")
    @Test
    void should_reissue_qr_code_when_valid_input() {
        // given
        given(qrCodeRepository.findById(qrCode.getId())).willReturn(Optional.of(qrCode));
        given(qrCodeRepository.save(any(QrCode.class))).willReturn(newQrCode);

        // when
        QrCodeResponse response = reissueQrCodeUsecase.execute(qrCode.getId());

        // then
        assertThat(response.id()).isEqualTo(newQrCode.getId());
        assertThat(response.sessionId()).isEqualTo(newQrCode.getSession().getId());
        assertThat(response.hashValue()).isEqualTo(newQrCode.getHashValue().getValue());
        assertThat(response.expiresAt()).isEqualTo(newQrCode.getExpiresAt());
    }

    @DisplayName("QR 코드 재발급 실패 - QR 코드 존재하지 않음")
    @Test
    void should_fail_reissue_qr_code_when_qr_code_not_found() {
        // given
        given(qrCodeRepository.findById(qrCode.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reissueQrCodeUsecase.execute(qrCode.getId()))
            .isInstanceOf(BusinessException.class)
            .hasMessage(SessionErrorCode.QR_NOT_FOUND.getMessage());
    }
}
