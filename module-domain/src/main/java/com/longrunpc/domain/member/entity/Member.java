package com.longrunpc.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;

import java.time.LocalDateTime;
import java.util.Objects;

import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.common.entity.BaseEntity;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.Phone;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    
    @Embedded
    private LoginId loginId;

    @Embedded
    private Password password;

    @Embedded
    private MemberName memberName;
    
    @Embedded
    private Phone phone;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Builder
    private Member(Long id, LoginId loginId, Password password, MemberName memberName, Phone phone, MemberRole role, MemberStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.loginId = Objects.requireNonNull(loginId);
        this.password = Objects.requireNonNull(password);
        this.memberName = Objects.requireNonNull(memberName);
        this.phone = Objects.requireNonNull(phone);
        this.role = Objects.requireNonNull(role);
        this.status = Objects.requireNonNull(status);
    }

    public static Member createMember(LoginId loginId, Password password, MemberName memberName, Phone phone) {
        return Member.builder()
            .loginId(loginId)
            .password(password)
            .memberName(memberName)
            .phone(phone)
            .role(MemberRole.USER)
            .status(MemberStatus.ACTIVE)
            .build();
    }

    public void changeMemberName(MemberName memberName) {
        this.memberName = Objects.requireNonNull(memberName);
    }

    public void changePhone(Phone phone) {
        this.phone = Objects.requireNonNull(phone);
    }

    public boolean isAdmin() {
        return this.role == MemberRole.ADMIN;
    }

    public boolean isWithdrawn() {
        return this.status == MemberStatus.WITHDRAWN;
    }

    public void withdraw() {
        if (this.status == MemberStatus.WITHDRAWN) {
            throw new BusinessException(MemberErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }
        this.status = MemberStatus.WITHDRAWN;
    }
}
