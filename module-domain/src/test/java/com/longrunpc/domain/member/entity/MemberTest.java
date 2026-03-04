package com.longrunpc.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.Phone;

@DisplayName("Member 엔티티 테스트")
class MemberTest {

    @DisplayName("createMember 메서드 테스트")
    @Nested
    class CreateMemberTest {

        @DisplayName("유효한 입력 시 멤버 생성")
        @Test
        void should_create_member_when_valid_input() {
            // given
            String loginId = "test@example.com";
            String password = "password";
            String name = "test";
            String phone = "01012345678";

            // when
            Member member = Member.createMember(new LoginId(loginId), new Password(password), new MemberName(name), new Phone(phone));

            // then
            assertThat(member.getLoginId()).isEqualTo(new LoginId(loginId));
            assertThat(member.getPassword()).isEqualTo(new Password(password));
            assertThat(member.getName()).isEqualTo(new MemberName(name));
            assertThat(member.getPhone()).isEqualTo(new Phone(phone));
            assertThat(member.getRole()).isEqualTo(MemberRole.USER);
            assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        }

        @DisplayName("null 값 입력 시 예외 발생")
        @Test
        void should_throw_exception_when_null_input() {
            // given
            String loginId = "test@example.com";
            String password = "password";
            String name = "test";
            String phone = "01012345678";

            // when & then
            assertThatThrownBy(() -> Member.createMember(null, new Password(password), new MemberName(name), new Phone(phone)))
                .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> Member.createMember(new LoginId(loginId), null, new MemberName(name), new Phone(phone)))
                .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> Member.createMember(new LoginId(loginId), new Password(password), null, new Phone(phone)))
                .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> Member.createMember(new LoginId(loginId), new Password(password), new MemberName(name), null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("changeMemberName 메서드 테스트")
    @Nested
    class ChangeMemberNameTest {

        @DisplayName("유효한 입력 시 멤버 이름 변경")
        @Test
        void should_change_member_name_when_valid_input() {
            // given
            Member member = Member.builder()
                .loginId(new LoginId("test@example.com"))
                .password(new Password("password"))
                .name(new MemberName("before"))
                .phone(new Phone("01012345678"))
                .role(MemberRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();

            // when
            member.changeMemberName(new MemberName("after"));

            // then
            assertThat(member.getName()).isEqualTo(new MemberName("after"));
        }

        @DisplayName("null 값 입력 시 예외 발생")
        @Test
        void should_throw_exception_when_null_input() {
            // given
            Member member = Member.builder()
                .loginId(new LoginId("test@example.com"))
                .password(new Password("password"))
                .name(new MemberName("before"))
                .phone(new Phone("01012345678"))
                .role(MemberRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();

            // when & then
            assertThatThrownBy(() -> member.changeMemberName(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("changePhone 메서드 테스트")
    @Nested
    class ChangePhoneTest {

        @DisplayName("유효한 입력 시 멤버 전화번호 변경")
        @Test
        void should_change_member_phone_when_valid_input() {
            // given
            Member member = Member.builder()
                .loginId(new LoginId("test@example.com"))
                .password(new Password("password"))
                .name(new MemberName("before"))
                .phone(new Phone("01012345678"))
                .role(MemberRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();

            // when
            member.changePhone(new Phone("01012345678"));

            // then
            assertThat(member.getPhone()).isEqualTo(new Phone("01012345678"));
        }

        @DisplayName("null 값 입력 시 예외 발생")
        @Test
        void should_throw_exception_when_null_input() {
            // given
            Member member = Member.builder()
                .loginId(new LoginId("test@example.com"))
                .password(new Password("password"))
                .name(new MemberName("before"))
                .phone(new Phone("01012345678"))
                .role(MemberRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();

            // when & then
            assertThatThrownBy(() -> member.changePhone(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("isAdmin 메서드 테스트")
    @Nested
    class IsAdminTest {

        @DisplayName("ADMIN 확인")
        @Test
        void should_return_true_when_admin() {
            // given
            Member member = Member.builder()
                .loginId(new LoginId("test@example.com"))
                .password(new Password("password"))
                .name(new MemberName("before"))
                .phone(new Phone("01012345678"))
                .role(MemberRole.ADMIN)
                .status(MemberStatus.ACTIVE)
                .build();

            // when
            boolean isAdmin = member.isAdmin();

            // then
            assertThat(isAdmin).isTrue();
        }

        @DisplayName("USER 확인")
        @Test
        void should_return_false_when_user() {
            // given
            Member member = Member.builder()
                .loginId(new LoginId("test@example.com"))
                .password(new Password("password"))
                .name(new MemberName("before"))
                .phone(new Phone("01012345678"))
                .role(MemberRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();

            // when
            boolean isAdmin = member.isAdmin();

            // then
            assertThat(isAdmin).isFalse();
        }
    }

    @DisplayName("isWithdrawn 메서드 테스트")
    @Nested
    class IsWithdrawnTest {

        @DisplayName("WITHDRAWN 확인")
        @Test
        void should_return_true_when_withdrawn() {
            // given
            Member member = Member.builder()
                .loginId(new LoginId("test@example.com"))
                .password(new Password("password"))
                .name(new MemberName("before"))
                .phone(new Phone("01012345678"))
                .role(MemberRole.USER)
                .status(MemberStatus.WITHDRAWN)
                .build();

            // when
            boolean isWithdrawn = member.isWithdrawn();

            // then
            assertThat(isWithdrawn).isTrue();
        }

        @DisplayName("ACTIVE 확인")
        @Test
        void should_return_false_when_active() {
            // given
            Member member = Member.builder()
                .loginId(new LoginId("test@example.com"))
                .password(new Password("password"))
                .name(new MemberName("before"))
                .phone(new Phone("01012345678"))
                .role(MemberRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();

            // when
            boolean isWithdrawn = member.isWithdrawn();

            // then
            assertThat(isWithdrawn).isFalse();
        }

        @DisplayName("INACTIVE 확인")
        @Test
        void should_return_false_when_inactive() {
            // given
            Member member = Member.builder()
                .loginId(new LoginId("test@example.com"))
                .password(new Password("password"))
                .name(new MemberName("before"))
                .phone(new Phone("01012345678"))
                .role(MemberRole.USER)
                .status(MemberStatus.INACTIVE)
                .build();

            // when
            boolean isWithdrawn = member.isWithdrawn();

            // then
            assertThat(isWithdrawn).isFalse();
        }
    }
}
