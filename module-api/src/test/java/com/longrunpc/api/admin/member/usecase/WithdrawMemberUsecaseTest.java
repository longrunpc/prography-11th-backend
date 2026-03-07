package com.longrunpc.api.admin.member.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;

@DisplayName("WithdrawMemberUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class WithdrawMemberUsecaseTest {

    @InjectMocks
    private WithdrawMemberUsecase withdrawMemberUsecase;
    @Mock
    private MemberRepository memberRepository;

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
            .build();
    }

    @DisplayName("회원 탈퇴 성공")
    @Test
    void should_withdraw_member_when_valid_input() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        // when
        withdrawMemberUsecase.execute(1L);

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
    }

    @DisplayName("회원 탈퇴 실패 - 회원 존재하지 않음")
    @Test
    void should_fail_when_member_not_found() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> withdrawMemberUsecase.execute(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 탈퇴 실패 - 이미 탈퇴한 회원")
    @Test
    void should_fail_when_member_already_withdrawn() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        member.withdraw();
    
        assertThatThrownBy(() -> withdrawMemberUsecase.execute(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.MEMBER_ALREADY_WITHDRAWN.getMessage());
    }
}
