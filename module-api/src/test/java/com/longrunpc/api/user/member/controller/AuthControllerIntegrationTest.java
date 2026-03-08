package com.longrunpc.api.user.member.controller;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longrunpc.api.AttendanceApplication;
import com.longrunpc.api.user.member.dto.request.LoginMemberRequest;
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
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("로그인 성공 시 회원 정보를 응답한다")
    @Test
    void should_return_member_when_login_success() throws Exception {
        // given
        String rawPassword = "password123!";
        Member savedMember = Objects.requireNonNull(memberRepository.save(Member.createMember(
            new LoginId("test@example.com"),
            new Password(passwordEncoder.encode(rawPassword)),
            new MemberName("테스트유저"),
            new Phone("01012345678")
        )));

        LoginMemberRequest request = new LoginMemberRequest("test@example.com", rawPassword);

        // when & then
        mockMvc.perform(post("/auth/login")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(savedMember.getId()))
            .andExpect(jsonPath("$.data.loginId").value("test@example.com"))
            .andExpect(jsonPath("$.data.name").value("테스트유저"))
            .andExpect(jsonPath("$.data.phone").value("01012345678"))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @DisplayName("로그인 실패 시 MEMBER_001 에러를 응답한다")
    @Test
    void should_return_login_failed_when_password_not_match() throws Exception {
        // given
        Objects.requireNonNull(memberRepository.save(Member.createMember(
            new LoginId("test@example.com"),
            new Password(passwordEncoder.encode("password123!")),
            new MemberName("테스트유저"),
            new Phone("01012345678")
        )));

        LoginMemberRequest request = new LoginMemberRequest("test@example.com", "wrong-password");

        // when & then
        mockMvc.perform(post("/auth/login")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.error.code").value(MemberErrorCode.LOGIN_FAILED.getCode()))
            .andExpect(jsonPath("$.error.message").value(MemberErrorCode.LOGIN_FAILED.getMessage()));
    }
}
