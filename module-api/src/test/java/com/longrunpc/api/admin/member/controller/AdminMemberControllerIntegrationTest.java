package com.longrunpc.api.admin.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longrunpc.api.AttendanceApplication;
import com.longrunpc.api.admin.member.dto.request.RegisterMemberRequest;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.vo.CohortName;
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
class AdminMemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원가입 성공 시 회원 상세 정보를 응답한다")
    @Test
    void should_register_member_when_valid_request() throws Exception {
        // given
        Cohort cohort = Objects.requireNonNull(cohortRepository.save(Cohort.builder()
            .generation(new Generation(11))
            .cohortName(new CohortName("11기"))
            .build()));

        RegisterMemberRequest request = new RegisterMemberRequest(
            "signup-user@example.com",
            "password123!",
            "가입유저",
            "01011112222",
            cohort.getId(),
            null,
            null
        );

        // when & then
        mockMvc.perform(post("/admin/members")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.loginId").value("signup-user@example.com"))
            .andExpect(jsonPath("$.data.name").value("가입유저"))
            .andExpect(jsonPath("$.data.phone").value("01011112222"))
            .andExpect(jsonPath("$.data.generation").value(11))
            .andExpect(jsonPath("$.data.partName").isEmpty())
            .andExpect(jsonPath("$.data.teamName").isEmpty())
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("회원가입 실패 시 중복 로그인 아이디 에러를 응답한다")
    @Test
    void should_fail_when_duplicate_login_id() throws Exception {
        // given
        Objects.requireNonNull(memberRepository.save(Member.createMember(
            new LoginId("duplicate@example.com"),
            new Password("encoded-password"),
            new MemberName("기존유저"),
            new Phone("01099998888")
        )));

        RegisterMemberRequest request = new RegisterMemberRequest(
            "duplicate@example.com",
            "password123!",
            "신규유저",
            "01011112222",
            1L,
            null,
            null
        );

        // when & then
        mockMvc.perform(post("/admin/members")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(MemberErrorCode.DUPLICATE_LOGIN_ID.getCode()))
            .andExpect(jsonPath("$.error.message").value(MemberErrorCode.DUPLICATE_LOGIN_ID.getMessage()));
    }
}
