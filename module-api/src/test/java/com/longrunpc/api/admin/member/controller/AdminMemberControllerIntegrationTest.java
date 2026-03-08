package com.longrunpc.api.admin.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasItem;

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
import com.longrunpc.api.admin.member.dto.request.UpdateMemberRequest;
import com.longrunpc.api.admin.member.dto.request.RegisterMemberRequest;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.entity.CohortMember;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
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

    @Autowired
    private CohortMemberRepository cohortMemberRepository;

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

    @DisplayName("회원 대시보드 조회 성공")
    @Test
    void should_read_member_dashboard() throws Exception {
        // given
        Cohort cohort = createCohort(11, "11기");
        Member member = createMember("dashboard@example.com", "대시보드회원", "01077778888");
        createCohortMember(member, cohort);

        // when & then
        mockMvc.perform(get("/admin/members")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalElements").isNumber())
            .andExpect(jsonPath("$.data.content[*].loginId", hasItem("dashboard@example.com")))
            .andExpect(jsonPath("$.data.content[*].name", hasItem("대시보드회원")))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("회원 상세 조회 성공")
    @Test
    void should_read_member_detail() throws Exception {
        // given
        Cohort cohort = createCohort(12, "12기");
        Member member = createMember("detail@example.com", "상세회원", "01033334444");
        createCohortMember(member, cohort);

        // when & then
        mockMvc.perform(get("/admin/members/{id}", member.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(member.getId()))
            .andExpect(jsonPath("$.data.loginId").value("detail@example.com"))
            .andExpect(jsonPath("$.data.name").value("상세회원"))
            .andExpect(jsonPath("$.data.generation").value(12))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("회원 정보 수정 성공")
    @Test
    void should_update_member() throws Exception {
        // given
        Cohort cohort = createCohort(13, "13기");
        Member member = createMember("update@example.com", "수정전", "01055556666");
        createCohortMember(member, cohort);

        UpdateMemberRequest request = new UpdateMemberRequest(
            "수정후",
            "01000001111",
            null,
            null,
            null
        );

        // when & then
        mockMvc.perform(put("/admin/members/{id}", member.getId())
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(member.getId()))
            .andExpect(jsonPath("$.data.name").value("수정후"))
            .andExpect(jsonPath("$.data.phone").value("01000001111"))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("회원 삭제(탈퇴) 성공")
    @Test
    void should_withdraw_member() throws Exception {
        // given
        Member member = createMember("withdraw@example.com", "탈퇴회원", "01012121212");

        // when & then
        mockMvc.perform(delete("/admin/members/{id}", member.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(member.getId()))
            .andExpect(jsonPath("$.data.loginId").value("withdraw@example.com"))
            .andExpect(jsonPath("$.data.status").value("WITHDRAWN"))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    private Cohort createCohort(int generation, String cohortName) {
        return Objects.requireNonNull(cohortRepository.save(Cohort.builder()
            .generation(new Generation(generation))
            .cohortName(new CohortName(cohortName))
            .build()));
    }

    private Member createMember(String loginId, String name, String phone) {
        return Objects.requireNonNull(memberRepository.save(Member.createMember(
            new LoginId(loginId),
            new Password("password"),
            new MemberName(name),
            new Phone(phone)
        )));
    }

    private CohortMember createCohortMember(Member member, Cohort cohort) {
        return Objects.requireNonNull(cohortMemberRepository.save(CohortMember.createCohortMember(member, cohort, null, null)));
    }
}
