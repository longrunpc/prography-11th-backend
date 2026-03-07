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

import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.PartRepository;
import com.longrunpc.domain.cohort.repository.TeamRepository;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.api.admin.member.dto.request.UpdateMemberRequest;
import com.longrunpc.api.admin.member.dto.response.MemberDetailResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.Part;
import com.longrunpc.domain.cohort.entity.Team;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.CohortName;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.ExcusedCount;
import com.longrunpc.domain.cohort.vo.PartName;
import com.longrunpc.domain.cohort.vo.TeamName;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;

@DisplayName("UpdateMemberUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class UpdateMemberUsecaseTest {

    @InjectMocks
    private UpdateMemberUsecase updateMemberUsecase;
    @Mock
    private CohortMemberRepository cohortMemberRepository;
    @Mock
    private CohortRepository cohortRepository;
    @Mock
    private PartRepository partRepository;
    @Mock
    private TeamRepository teamRepository;

    private CohortMember cohortMember;
    private Cohort cohort;
    private Part part;
    private Team team;
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
        cohortMember = CohortMember.builder()
            .id(1L)
            .member(member)
            .cohort(cohort)
            .part(part)
            .team(team)
            .deposit(new Deposit(100_000))
            .excusedCount(new ExcusedCount(0))
            .build();
    }

    @DisplayName("회원 정보 수정 성공 - name 수정")
    @Test
    void should_update_member_info_when_name_is_updated() {
        // given
        UpdateMemberRequest request = new UpdateMemberRequest(
            "updatedName",
            null,
            null,
            null,
            null
        );
        given(cohortMemberRepository.findDetailByMemberId(1L)).willReturn(Optional.of(cohortMember));

        // when
        MemberDetailResponse result = updateMemberUsecase.execute(request, 1L);

        // then
        assertThat(result).isEqualTo(MemberDetailResponse.of(cohortMember));
    }

    @DisplayName("회원 정보 수정 성공 - phone 수정")
    @Test
    void should_update_member_info_when_phone_is_updated() {
        // given
        UpdateMemberRequest request = new UpdateMemberRequest(
            null,
            "01087654321",
            null,
            null,
            null
        );
        given(cohortMemberRepository.findDetailByMemberId(1L)).willReturn(Optional.of(cohortMember));

        // when
        MemberDetailResponse result = updateMemberUsecase.execute(request, 1L);

        // then
        assertThat(result).isEqualTo(MemberDetailResponse.of(cohortMember));
    }

    @DisplayName("회원 정보 수정 성공 - cohort 수정")
    @Test
    void should_update_member_info_when_cohort_is_updated() {
        // given
        UpdateMemberRequest request = new UpdateMemberRequest(
            null,
            null,
            cohort.getId(),
            null,
            null
        );
        given(cohortMemberRepository.findDetailByMemberId(1L)).willReturn(Optional.of(cohortMember));
        given(cohortRepository.findById(cohort.getId())).willReturn(Optional.of(cohort));

        // when
        MemberDetailResponse result = updateMemberUsecase.execute(request, 1L);

        // then
        assertThat(result).isEqualTo(MemberDetailResponse.of(cohortMember));
    }

    @DisplayName("회원 정보 수정 성공 - part 수정")
    @Test
    void should_update_member_info_when_part_is_updated() {
        // given
        UpdateMemberRequest request = new UpdateMemberRequest(
            null,
            null,
            null,
            part.getId(),
            null
        );
        given(cohortMemberRepository.findDetailByMemberId(1L)).willReturn(Optional.of(cohortMember));
        given(partRepository.findById(part.getId())).willReturn(Optional.of(part));

        // when
        MemberDetailResponse result = updateMemberUsecase.execute(request, 1L);

        // then
        assertThat(result).isEqualTo(MemberDetailResponse.of(cohortMember));
    }

    @DisplayName("회원 정보 수정 성곱 - team 수정")
    @Test
    void should_update_member_info_when_team_is_updated() {
        // given
        UpdateMemberRequest request = new UpdateMemberRequest(
            null,
            null,
            null,
            null,
            team.getId()
        );
        given(cohortMemberRepository.findDetailByMemberId(1L)).willReturn(Optional.of(cohortMember));
        given(teamRepository.findById(1L)).willReturn(Optional.of(team));

        // when
        MemberDetailResponse result = updateMemberUsecase.execute(request, 1L);

        // then
        assertThat(result).isEqualTo(MemberDetailResponse.of(cohortMember));
    }

    @DisplayName("회원 정보 수정 실패 - 회원 존재하지 않음")
    @Test
    void should_fail_when_member_not_found() {
        // given
        UpdateMemberRequest request = new UpdateMemberRequest(
            "test",
            "01012345678",
            cohort.getId(),
            part.getId(),
            team.getId()
        );
        given(cohortMemberRepository.findDetailByMemberId(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateMemberUsecase.execute(request, 1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 정보 수정 실패 - 기수 존재하지 않음")
    @Test
    void should_fail_when_cohort_not_found() {
        // given
        UpdateMemberRequest request = new UpdateMemberRequest(
            "test",
            "01012345678",
            cohort.getId(),
            part.getId(),
            team.getId()
        );
        given(cohortMemberRepository.findDetailByMemberId(1L)).willReturn(Optional.of(cohortMember));
        given(cohortRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateMemberUsecase.execute(request, 1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.COHORT_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 정보 수정 실패 - 파트 존재하지 않음")
    @Test
    void should_fail_when_part_not_found() {
        // given
        UpdateMemberRequest request = new UpdateMemberRequest(
            "test",
            "01012345678",
            cohort.getId(),
            part.getId(),
            team.getId()
        );
        given(cohortMemberRepository.findDetailByMemberId(1L)).willReturn(Optional.of(cohortMember));
        given(cohortRepository.findById(1L)).willReturn(Optional.of(cohort));
        given(partRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateMemberUsecase.execute(request, 1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.PART_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 정보 수정 실패 - 팀 존재하지 않음")
    @Test
    void should_fail_when_team_not_found() {
        // given
        UpdateMemberRequest request = new UpdateMemberRequest(
            "test",
            "01012345678",
            cohort.getId(),
            part.getId(),
            team.getId()
        );
        given(cohortMemberRepository.findDetailByMemberId(1L)).willReturn(Optional.of(cohortMember));
        given(cohortRepository.findById(1L)).willReturn(Optional.of(cohort));
        given(partRepository.findById(1L)).willReturn(Optional.of(part));
        given(teamRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateMemberUsecase.execute(request, 1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(CohortErrorCode.TEAM_NOT_FOUND.getMessage());
    }
}
