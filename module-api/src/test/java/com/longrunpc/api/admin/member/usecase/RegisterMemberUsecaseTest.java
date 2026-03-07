package com.longrunpc.api.admin.member.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.longrunpc.api.admin.member.dto.request.RegisterMemberRequest;
import com.longrunpc.api.admin.member.dto.response.AdminMemberResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.Part;
import com.longrunpc.domain.cohort.entity.Team;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.domain.cohort.repository.PartRepository;
import com.longrunpc.domain.cohort.repository.TeamRepository;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.PartName;
import com.longrunpc.domain.cohort.vo.TeamName;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.Phone;

@DisplayName("회원 가입 Usecase 테스트")
@ExtendWith(MockitoExtension.class)
public class RegisterMemberUsecaseTest {

    @InjectMocks
    private RegisterMemberUsecase registerMemberUsecase;
    
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CohortMemberRepository cohortMemberRepository;
    @Mock
    private DepositHistoryRepository depositHistoryRepository;
    @Mock
    private CohortRepository cohortRepository;
    @Mock
    private PartRepository partRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private Cohort cohort;
    private Part part;
    private Team team;

    @BeforeEach
    void setUp() {
        cohort = Cohort.builder()
            .id(1L)
            .generation(new Generation(11))
            .cohortName(new CohortName("11기"))
            .build();
        part = Part.builder()
            .id(1L)
            .partName(new PartName("test"))
            .cohort(cohort)
            .build();
        team = Team.builder()
            .id(1L)
            .teamName(new TeamName("test"))
            .cohort(cohort)
            .build();
    }

    @DisplayName("회원 가입 성공")
    @Test
    void should_register_member_when_valid_input() {
        // given
        RegisterMemberRequest request = new RegisterMemberRequest(
            "test@example.com",
            "password",
            "test",
            "01012345678",
            1L,
            1L,
            1L
        );
        given(memberRepository.findByLoginId(new LoginId("test@example.com"))).willReturn(Optional.empty());
        given(cohortRepository.findById(1L)).willReturn(Optional.of(cohort));
        given(partRepository.findById(1L)).willReturn(Optional.of(part));
        given(teamRepository.findById(1L)).willReturn(Optional.of(team));
        given(passwordEncoder.encode("password")).willReturn("encodedPassword");
        given(memberRepository.save(any(Member.class))).willReturn(Member.builder()
            .id(1L)
            .loginId(new LoginId("test@example.com"))
            .password(new Password("encodedPassword"))
            .memberName(new MemberName("test"))
            .phone(new Phone("01012345678"))
            .role(MemberRole.MEMBER)
            .status(MemberStatus.ACTIVE)
            .build());


        // when
        AdminMemberResponse result = registerMemberUsecase.execute(request);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.loginId()).isEqualTo("test@example.com");
        assertThat(result.name()).isEqualTo("test");
        assertThat(result.phone()).isEqualTo("01012345678");
        assertThat(result.status()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(result.role()).isEqualTo(MemberRole.MEMBER);
        assertThat(result.generation()).isEqualTo(cohort.getGeneration().getValue());
        assertThat(result.partName()).isEqualTo(part.getPartName().getValue());
        assertThat(result.teamName()).isEqualTo(team.getTeamName().getValue());
    }

    @DisplayName("회원 가입 실패 - 아이디 중복")
    @Test
    void should_fail_when_duplicate_login_id() {
        // given
        RegisterMemberRequest request = new RegisterMemberRequest(
            "test@example.com",
            "password",
            "test",
            "01012345678",
            1L,
            1L,
            1L
        );
        given(memberRepository.findByLoginId(new LoginId("test@example.com"))).willReturn(Optional.of(Member.builder()
            .id(1L)
            .loginId(new LoginId("test@example.com"))
            .password(new Password("encodedPassword"))
            .memberName(new MemberName("test"))
            .phone(new Phone("01012345678"))
            .role(MemberRole.MEMBER)
            .status(MemberStatus.ACTIVE)
            .build()));

        // when & then
        assertThatThrownBy(() -> registerMemberUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.DUPLICATE_LOGIN_ID.getMessage());
    }

    @DisplayName("회원 가입 실패 - 기수 존재하지 않음")
    @Test
    void should_fail_when_cohort_not_found() {
        // given
        RegisterMemberRequest request = new RegisterMemberRequest(
            "test@example.com",
            "password",
            "test",
            "01012345678",
            1L,
            1L,
            1L
        );
        given(memberRepository.findByLoginId(new LoginId("test@example.com"))).willReturn(Optional.empty());
        given(cohortRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> registerMemberUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.COHORT_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 가입 실패 - 파트 존재하지 않음")
    @Test
    void should_fail_when_part_not_found() {
        // given
        RegisterMemberRequest request = new RegisterMemberRequest(
            "test@example.com",
            "password",
            "test",
            "01012345678",
            1L,
            1L,
            1L
        );
        given(memberRepository.findByLoginId(new LoginId("test@example.com"))).willReturn(Optional.empty());
        given(cohortRepository.findById(1L)).willReturn(Optional.of(cohort));
        given(partRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> registerMemberUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.PART_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 가입 실패 - 팀 존재하지 않음")
    @Test
    void should_fail_when_team_not_found() {
        // given
        RegisterMemberRequest request = new RegisterMemberRequest(
            "test@example.com",
            "password",
            "test",
            "01012345678",
            1L,
            1L,
            1L
        );
        given(memberRepository.findByLoginId(new LoginId("test@example.com"))).willReturn(Optional.empty());
        given(cohortRepository.findById(1L)).willReturn(Optional.of(cohort));
        given(partRepository.findById(1L)).willReturn(Optional.of(part));
        given(teamRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> registerMemberUsecase.execute(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.TEAM_NOT_FOUND.getMessage());
    }
}