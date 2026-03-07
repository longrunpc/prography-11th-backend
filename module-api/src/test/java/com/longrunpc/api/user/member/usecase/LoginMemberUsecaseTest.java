package com.longrunpc.api.user.member.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.longrunpc.api.user.member.dto.request.LoginMemberRequest;
import com.longrunpc.api.user.member.dto.response.MemberResponse;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;

@DisplayName("LoginMemberUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class LoginMemberUsecaseTest {

    @InjectMocks
    private LoginMemberUsecase loginMemberUsecase;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private Member member;
    @BeforeEach
    void setUp() {
        member = Member.builder()
            .id(1L)
            .loginId(new LoginId("test@example.com"))
            .password(new Password("password"))
            .memberName(new MemberName("test"))
            .phone(new Phone("01012345678"))
            .role(MemberRole.MEMBER)
            .status(MemberStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @DisplayName("로그인 성공")
    @Test
    void should_login_when_valid_input() {
        // given
        LoginMemberRequest request = new LoginMemberRequest(
            "test@example.com",
            "password"
        );
        given(memberRepository.findByLoginId(new LoginId("test@example.com"))).willReturn(Optional.of(member));
        given(passwordEncoder.matches("password", "password")).willReturn(true);

        // when
        MemberResponse result = loginMemberUsecase.execute(request);

        // then
        assertThat(result).isEqualTo(MemberResponse.of(member));
    }

    @DisplayName("로그인 실패 - 아이디 존재하지 않음")
    @Test
    void should_fail_when_login_id_not_found() {
        // given
        LoginMemberRequest request = new LoginMemberRequest(
            "test@example.com",
            "password"
        );
        given(memberRepository.findByLoginId(new LoginId("test@example.com"))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loginMemberUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.LOGIN_FAILED.getMessage());
    }

    @DisplayName("로그인 실패 - 비밀번호 불일치")
    @Test
    void should_fail_when_password_not_match() {
        // given
        LoginMemberRequest request = new LoginMemberRequest(
            "test@example.com",
            "password"
        );

        given(memberRepository.findByLoginId(new LoginId("test@example.com"))).willReturn(Optional.of(member));
        given(passwordEncoder.matches("password", "password")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> loginMemberUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.LOGIN_FAILED.getMessage());
    }

    @DisplayName("로그인 실패 - 탈퇴 회원")
    @Test
    void should_fail_when_member_withdrawn() {
        // given
        LoginMemberRequest request = new LoginMemberRequest(
            "test@example.com",
            "password"
        );
        given(memberRepository.findByLoginId(new LoginId("test@example.com"))).willReturn(Optional.of(member));
        given(passwordEncoder.matches("password", "password")).willReturn(true);
        member.withdraw();

        // when & then
        assertThatThrownBy(() -> loginMemberUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.MEMBER_WITHDRAWN.getMessage());
    }
}
