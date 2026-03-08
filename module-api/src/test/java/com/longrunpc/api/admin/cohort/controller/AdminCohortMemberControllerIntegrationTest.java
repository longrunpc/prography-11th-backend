package com.longrunpc.api.admin.cohort.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.AttendanceApplication;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.cohort.entity.DepositType;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.repository.DepositHistoryRepository;
import com.longrunpc.domain.cohort.vo.Description;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.Phone;

@SpringBootTest(classes = AttendanceApplication.class)
@AutoConfigureMockMvc
@Transactional
class AdminCohortMemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private CohortMemberRepository cohortMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DepositHistoryRepository depositHistoryRepository;

    @DisplayName("기수 회원 보증금 이력 조회 성공")
    @Test
    void should_read_cohort_member_deposit_histories() throws Exception {
        Cohort cohort = findCurrentCohort();
        Member member = createMember("deposit-history@example.com", "보증금이력회원", "01088889999");
        CohortMember cohortMember = createCohortMember(member, cohort);

        DepositHistory first = createDepositHistory(cohortMember, DepositType.INITIAL, 100000, 100000, "초기 보증금");
        DepositHistory second = createDepositHistory(cohortMember, DepositType.PENALTY, 5000, 95000, "지각 패널티");

        mockMvc.perform(get("/admin/cohort-members/{cohortMemberId}/deposits", cohortMember.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(2))
            // 최신순 정렬 확인
            .andExpect(jsonPath("$.data[0].id").value(second.getId()))
            .andExpect(jsonPath("$.data[0].cohortMemberId").value(cohortMember.getId()))
            .andExpect(jsonPath("$.data[0].depositType").value("PENALTY"))
            .andExpect(jsonPath("$.data[0].amount").value(5000))
            .andExpect(jsonPath("$.data[0].balanceAfter").value(95000))
            .andExpect(jsonPath("$.data[0].attendanceId").isEmpty())
            .andExpect(jsonPath("$.data[0].description").value("지각 패널티"))
            .andExpect(jsonPath("$.data[0].createdAt").isString())
            .andExpect(jsonPath("$.data[0].createdAt").isNotEmpty())
            .andExpect(jsonPath("$.data[1].id").value(first.getId()))
            .andExpect(jsonPath("$.data[1].cohortMemberId").value(cohortMember.getId()))
            .andExpect(jsonPath("$.data[1].depositType").value("INITIAL"))
            .andExpect(jsonPath("$.data[1].amount").value(100000))
            .andExpect(jsonPath("$.data[1].balanceAfter").value(100000))
            .andExpect(jsonPath("$.data[1].attendanceId").isEmpty())
            .andExpect(jsonPath("$.data[1].description").value("초기 보증금"))
            .andExpect(jsonPath("$.data[1].createdAt").isString())
            .andExpect(jsonPath("$.data[1].createdAt").isNotEmpty())
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("기수 회원 보증금 이력 조회 실패 - 기수 회원 없음")
    @Test
    void should_fail_read_deposit_histories_when_cohort_member_not_found() throws Exception {
        mockMvc.perform(get("/admin/cohort-members/{cohortMemberId}/deposits", 999999L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(CohortErrorCode.COHORT_MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(CohortErrorCode.COHORT_MEMBER_NOT_FOUND.getMessage()));
    }

    @DisplayName("기수 회원 보증금 이력 조회 실패 - 잘못된 cohortMemberId 타입")
    @Test
    void should_fail_read_deposit_histories_when_invalid_cohort_member_id_type() throws Exception {
        mockMvc.perform(get("/admin/cohort-members/{cohortMemberId}/deposits", "invalid-id"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(GlobalErrorCode.INTERNAL_ERROR.getCode()))
            .andExpect(jsonPath("$.error.message").value(GlobalErrorCode.INTERNAL_ERROR.getMessage()));
    }

    private Cohort findCurrentCohort() {
        return cohortRepository.findByGeneration(new Generation(11))
            .orElseThrow(() -> new IllegalStateException("cohort 11 should exist"));
    }

    @SuppressWarnings("null")
    private Member createMember(String loginId, String name, String phone) {
        return memberRepository.save(Member.createMember(
            new LoginId(loginId),
            new Password("password"),
            new MemberName(name),
            new Phone(phone)
        ));
    }

    @SuppressWarnings("null")
    private CohortMember createCohortMember(Member member, Cohort cohort) {
        return cohortMemberRepository.save(CohortMember.createCohortMember(member, cohort, null, null));
    }

    @SuppressWarnings("null")
    private DepositHistory createDepositHistory(
        CohortMember cohortMember,
        DepositType depositType,
        int amount,
        int balanceAfter,
        String description
    ) {
        return Objects.requireNonNull(depositHistoryRepository.save(DepositHistory.builder()
            .cohortMember(cohortMember)
            .attendance(null)
            .depositType(depositType)
            .amount(amount)
            .balanceAfter(balanceAfter)
            .description(new Description(description))
            .build()));
    }
}
