package com.longrunpc.api.admin.member.usecase;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.longrunpc.api.admin.member.dto.request.MemberDashboardRequest;
import com.longrunpc.api.admin.member.dto.response.MemberDashboardResponse;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;

@DisplayName("GetMemberDashboardUsecase 테스트")
@ExtendWith(MockitoExtension.class)
public class ReadMemberDashboardUsecaseTest {
    @InjectMocks
    private ReadMemberDashboardUsecase getMemberDashboardUsecase;
    
    @Mock
    private CohortMemberRepository cohortMemberRepository;

    private MemberDashboardRequest request;
    private MemberDashboardResponse response;

    @BeforeEach
    void setUp() {
        request = new MemberDashboardRequest(
            0,
            10,
            null,
            null,
            null,
            null,
            null,
            null
        );
        response = MemberDashboardResponse.builder()
            .content(Collections.emptyList())
            .page(0)
            .size(10)
            .totalElements(0)
            .totalPages(0)
            .build();
    }

    @DisplayName("회원 대시보드 조회 성공")
    @Test
    void should_get_member_dashboard_when_valid_input() {
        // given
        given(cohortMemberRepository.findAllConditions(request.status(), request.searchType(), request.searchValue())).willReturn(Collections.emptyList());

        // when
        MemberDashboardResponse result = getMemberDashboardUsecase.execute(request);

        // then
        assertThat(result).isEqualTo(response);
    }
}
