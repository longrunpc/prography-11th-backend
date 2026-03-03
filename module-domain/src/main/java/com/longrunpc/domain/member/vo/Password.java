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
public class Password {

    @Column(name = "password", nullable = false)
    private String value;

    public Password(String value) {
        validate(value);
        this.value = value;
    }
    
    private void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("비밀번호가 비어 있습니다.");
        }
    }
}
