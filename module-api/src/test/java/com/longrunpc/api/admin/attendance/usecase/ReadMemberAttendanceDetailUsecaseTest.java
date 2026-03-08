package com.longrunpc.api.admin.attendance.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.api.admin.attendance.dto.response.MemberAttendanceResponse;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.member.entity.MemberRole;
import com.longrunpc.domain.member.entity.MemberStatus;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Phone;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.Part;
import com.longrunpc.domain.cohort.entity.Team;
import com.longrunpc.domain.cohort.vo.Deposit;
import com.longrunpc.domain.cohort.vo.ExcusedCount;
import com.longrunpc.domain.cohort.vo.PartName;
import com.longrunpc.domain.cohort.vo.TeamName;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.cohort.vo.CohortName;

@DisplayName("ReadMemberAttendanceDetailUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class ReadMemberAttendanceDetailUsecaseTest {
    @InjectMocks
    private ReadMemberAttendanceDetailUsecase readMemberAttendanceDetailUsecase;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private CohortMemberRepository cohortMemberRepository;
    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private CohortMember cohortMember;
    private List<Attendance> attendances;
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
        member = Member.builder()
            .id(1L)
            .loginId(new LoginId("test@test.com"))
            .password(new Password("password"))
            .memberName(new MemberName("test"))
            .phone(new Phone("01012345678"))
            .role(MemberRole.MEMBER)
            .status(MemberStatus.ACTIVE)
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
        attendances = List.of();
    }


    @DisplayName("회원 출결 상세 조회 성공")
    @Test
    void should_get_member_attendance_detail_when_valid_input() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(cohortMemberRepository.findDetailByMemberId(1L)).willReturn(Optional.of(cohortMember));
        given(attendanceRepository.findAllByMemberId(1L)).willReturn(List.of());

        // when
        MemberAttendanceResponse result = readMemberAttendanceDetailUsecase.execute(1L);

        // then
        assertThat(result).isEqualTo(MemberAttendanceResponse.of(cohortMember, attendances));
    }

    @DisplayName("회원 출결 상세 조회 실패 - 회원 존재하지 않음")
    @Test
    void should_fail_to_get_member_attendance_detail_when_member_not_found() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readMemberAttendanceDetailUsecase.execute(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }
}