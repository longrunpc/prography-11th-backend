package com.longrunpc.api.user.member.controller;

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
import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.repository.MemberRepository;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.Phone;

@SpringBootTest(classes = AttendanceApplication.class)
@AutoConfigureMockMvc
@Transactional
class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원 조회 성공")
    @Test
    void should_read_member() throws Exception {
        // given
        Member member = Objects.requireNonNull(memberRepository.save(Member.createMember(
            new LoginId("member-read@example.com"),
            new Password("password"),
            new MemberName("조회회원"),
            new Phone("01022223333")
        )));

        // when & then
        mockMvc.perform(get("/members/{id}", member.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(member.getId()))
            .andExpect(jsonPath("$.data.loginId").value("member-read@example.com"))
            .andExpect(jsonPath("$.data.name").value("조회회원"))
            .andExpect(jsonPath("$.data.phone").value("01022223333"))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("회원 조회 실패 - 존재하지 않는 회원")
    @Test
    void should_fail_when_member_not_found() throws Exception {
        // when & then
        mockMvc.perform(get("/members/{id}", 999999L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(MemberErrorCode.MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.error.message").value(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()));
    }

    @DisplayName("회원 조회 실패 - 잘못된 id 타입")
    @Test
    void should_fail_when_member_id_is_invalid_type() throws Exception {
        // when & then
        mockMvc.perform(get("/members/{id}", "invalid-id"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(GlobalErrorCode.INTERNAL_ERROR.getCode()))
            .andExpect(jsonPath("$.error.message").value(GlobalErrorCode.INTERNAL_ERROR.getMessage()));
    }
}
