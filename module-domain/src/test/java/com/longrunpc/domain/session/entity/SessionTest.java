package com.longrunpc.domain.session.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;

@DisplayName("Session 엔티티 테스트")
public class SessionTest {

    private Cohort cohort;

    @BeforeEach
    void setUp() {
        // given
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation("11기"))
            .build();
    }

    @DisplayName("createSession 메서드 테스트")
    @Nested
    class CreateSessionTest {

        @DisplayName("유효한 입력 시 정상 생성")
        @Test
        void should_create_session_when_valid_input() {
            // given
            SessionTitle title = new SessionTitle("test");
            LocalDate sessionDate = LocalDate.now().plusDays(1);
            LocalTime sessionTime = LocalTime.now().plusHours(1);
            SessionLocation sessionLocation = new SessionLocation("강남역");

            // when
            Session session = Session.createSession(cohort, title, sessionDate, sessionTime, sessionLocation);

            // then
            assertThat(session.getTitle()).isEqualTo(title);
            assertThat(session.getSessionDate()).isEqualTo(sessionDate);
            assertThat(session.getSessionTime()).isEqualTo(sessionTime);
            assertThat(session.getSessionLocation()).isEqualTo(sessionLocation);
            assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.SCHEDULED);
        }

        @DisplayName("sessionDate 필드 null 시 예외 발생")
        @Test
        void should_throw_exception_when_session_date_is_null() {
            // given
            SessionTitle title = new SessionTitle("test");
            LocalDate sessionDate = null;
            LocalTime sessionTime = LocalTime.now().plusHours(1);
            SessionLocation sessionLocation = new SessionLocation("강남역");

            // when & then
            assertThatThrownBy(() -> Session.createSession(cohort, title, sessionDate, sessionTime, sessionLocation))
                .isInstanceOf(BusinessException.class);
        }

        @DisplayName("sessionTime 필드 null 시 예외 발생")
        @Test
        void should_throw_exception_when_session_time_is_null() {
            // given
            SessionTitle title = new SessionTitle("test");
            LocalDate sessionDate = LocalDate.now().plusDays(1);
            LocalTime sessionTime = null;
            SessionLocation sessionLocation = new SessionLocation("강남역");

            // when & then
            assertThatThrownBy(() -> Session.createSession(cohort, title, sessionDate, sessionTime, sessionLocation))
                .isInstanceOf(BusinessException.class);
        }

        @DisplayName("sessionLocation 필드 null 시 예외 발생")
        @Test
        void should_throw_exception_when_session_location_is_null() {
            // given
            SessionTitle title = new SessionTitle("test");
            LocalDate sessionDate = LocalDate.now().plusDays(1);
            LocalTime sessionTime = LocalTime.now().plusHours(1);
            SessionLocation sessionLocation = null;
        
            // when & then
            assertThatThrownBy(() -> Session.createSession(cohort, title, sessionDate, sessionTime, sessionLocation))
                .isInstanceOf(NullPointerException.class);
        }

        @DisplayName("session 시간이 현재시간 이전일 시 예외 발생")
        @Test
        void should_throw_exception_when_session_time_is_before_current_time() {
            // given
            SessionTitle title = new SessionTitle("test");
            LocalDate sessionDate = LocalDate.now();
            LocalTime sessionTime = LocalTime.now().minusHours(1);
            SessionLocation sessionLocation = new SessionLocation("강남역");

            // when & then
            assertThatThrownBy(() -> Session.createSession(cohort, title, sessionDate, sessionTime, sessionLocation))
                .isInstanceOf(BusinessException.class);
        }
    }

    @DisplayName("changeTitle 메서드 테스트")
    @Nested
    class ChangeTitleTest {
        @DisplayName("유효한 입력 시 정상 변경")
        @Test
        void should_change_title_when_valid_input() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            SessionTitle title = new SessionTitle("test2");

            // when
            session.changeTitle(title);

            // then
            assertThat(session.getTitle()).isEqualTo(title);
        }

        @DisplayName("session 상태가 CANCELLED 일 시 예외 발생")
        @Test
        void should_throw_exception_when_session_status_is_cancelled() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            session.cancel();
            SessionTitle title = new SessionTitle("test2");

            // when & then
            assertThatThrownBy(() -> session.changeTitle(title))
                .isInstanceOf(BusinessException.class);
        }

        @DisplayName("title 필드 null 시 예외 발생")
        @Test
        void should_throw_exception_when_title_is_null() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            SessionTitle title = null;

            // when & then
            assertThatThrownBy(() -> session.changeTitle(title))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("changeSessionDate 메서드 테스트")
    @Nested
    class ChangeSessionDateTest {
        @DisplayName("유효한 입력 시 정상 변경")
        @Test
        void should_change_session_date_when_valid_input() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            LocalDate sessionDate = LocalDate.now().plusDays(2);

            // when
            session.changeSessionDate(sessionDate);

            // then
            assertThat(session.getSessionDate()).isEqualTo(sessionDate);
        }

        @DisplayName("session 상태가 CANCELLED 일 시 예외 발생")
        @Test
        void should_throw_exception_when_session_status_is_cancelled() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            session.cancel();

            // when & then
            assertThatThrownBy(() -> session.changeSessionDate(LocalDate.now().plusDays(2)))
                .isInstanceOf(BusinessException.class);
        }

        @DisplayName("sessionDate 필드 null 시 예외 발생")
        @Test
        void should_throw_exception_when_session_date_is_null() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            LocalDate sessionDate = null;

            // when & then
            assertThatThrownBy(() -> session.changeSessionDate(sessionDate))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("changeSessionTime 메서드 테스트")
    @Nested
    class ChangeSessionTimeTest {
        @DisplayName("유효한 입력 시 정상 변경")
        @Test
        void should_change_session_time_when_valid_input() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            LocalTime sessionTime = LocalTime.now().plusHours(2);

            // when
            session.changeSessionTime(sessionTime);

            // then
            assertThat(session.getSessionTime()).isEqualTo(sessionTime);
        }

        @DisplayName("session 상태가 CANCELLED 일 시 예외 발생")
        @Test
        void should_throw_exception_when_session_status_is_cancelled() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            session.cancel();

            // when & then
            assertThatThrownBy(() -> session.changeSessionTime(LocalTime.now().plusHours(2)))
                .isInstanceOf(BusinessException.class);
        }

        @DisplayName("sessionTime 필드 null 시 예외 발생")
        @Test
        void should_throw_exception_when_session_time_is_null() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            LocalTime sessionTime = null;

            // when & then
            assertThatThrownBy(() -> session.changeSessionTime(sessionTime))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("changeSessionLocation 메서드 테스트")
    @Nested
    class ChangeSessionLocationTest {
        @DisplayName("유효한 입력 시 정상 변경")
        @Test
        void should_change_session_location_when_valid_input() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            SessionLocation sessionLocation = new SessionLocation("신촌역");

            // when
            session.changeSessionLocation(sessionLocation);

            // then
            assertThat(session.getSessionLocation()).isEqualTo(sessionLocation);
        }

        @DisplayName("session 상태가 CANCELLED 일 시 예외 발생")
        @Test
        void should_throw_exception_when_session_status_is_cancelled() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            session.cancel();

            // when & then
            assertThatThrownBy(() -> session.changeSessionLocation(new SessionLocation("신촌역")))
                .isInstanceOf(BusinessException.class);
        }

        @DisplayName("sessionLocation 필드 null 시 예외 발생")
        @Test
        void should_throw_exception_when_session_location_is_null() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            SessionLocation sessionLocation = null;

            // when & then
            assertThatThrownBy(() -> session.changeSessionLocation(sessionLocation))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("changeSessionStatus 메서드 테스트")
    @Nested
    class ChangeSessionStatusTest {
        @DisplayName("유효한 입력 시 정상 변경")
        @Test
        void should_change_session_status_when_valid_input() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            SessionStatus sessionStatus = SessionStatus.IN_PROGRESS;

            // when
            session.changeSessionStatus(sessionStatus);

            // then
            assertThat(session.getSessionStatus()).isEqualTo(sessionStatus);
        }

        @DisplayName("session 상태가 CANCELLED 일 시 예외 발생")
        @Test
        void should_throw_exception_when_session_status_is_cancelled() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            session.cancel();

            // when & then
            assertThatThrownBy(() -> session.changeSessionStatus(SessionStatus.CANCELLED))
                .isInstanceOf(BusinessException.class);
        }

        @DisplayName("sessionStatus 필드 null 시 예외 발생")
        @Test
        void should_throw_exception_when_session_status_is_null() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            SessionStatus sessionStatus = null;

            // when & then
            assertThatThrownBy(() -> session.changeSessionStatus(sessionStatus))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("cancel 메서드 테스트")
    @Nested
    class CancelTest {
        @DisplayName("유효한 입력 시 정상 취소")
        @Test
        void should_cancel_session_when_valid_input() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));

            // when
            session.cancel();

            // then
            assertThat(session.getSessionStatus()).isEqualTo(SessionStatus.CANCELLED);
        }

        @DisplayName("session 상태가 CANCELLED 일 시 예외 발생")
        @Test
        void should_throw_exception_when_session_status_is_cancelled() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            session.cancel();

            // when & then
            assertThatThrownBy(() -> session.cancel())
                .isInstanceOf(BusinessException.class);
        }
    }

    @DisplayName("isCancelled 메서드 테스트")
    @Nested
    class IsCancelledTest {
        @DisplayName("session 상태가 CANCELLED 일 시 true 반환")
        @Test
        void should_return_true_when_session_status_is_cancelled() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));
            session.cancel();

            // when & then
            assertThat(session.isCancelled()).isTrue();
        }

        @DisplayName("session 상태가 CANCELLED 아닐 시 false 반환")
        @Test
        void should_return_false_when_session_status_is_not_cancelled() {
            // given
            Session session = Session.createSession(cohort, new SessionTitle("test"), LocalDate.now().plusDays(1), LocalTime.now().plusHours(1), new SessionLocation("강남역"));

            // when & then
            assertThat(session.isCancelled()).isFalse();
        }
    }
}
