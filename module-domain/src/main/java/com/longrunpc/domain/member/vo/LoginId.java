package com.longrunpc.domain.member.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class LoginId {
    @Column(name = "login_id", nullable = false)
    private String value;

    public LoginId(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("로그인 아이디가 비어 있습니다.");
        }
    }
}
