package com.longrunpc.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;

import java.util.Objects;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private LoginId loginId;

    @Embedded
    private Password password;

    @Embedded
    private MemberName name;
    
    @Embedded
    private Phone phone;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Builder
    private Member(Long id, LoginId loginId, Password password, MemberName name, Phone phone, MemberRole role, MemberStatus status) {
        this.id = id;
        this.loginId = Objects.requireNonNull(loginId);
        this.password = Objects.requireNonNull(password);
        this.name = Objects.requireNonNull(name);
        this.phone = Objects.requireNonNull(phone);
        this.role = Objects.requireNonNull(role);
        this.status = Objects.requireNonNull(status);
    }

    public static Member createMember(LoginId loginId, Password password, MemberName name, Phone phone) {
        return Member.builder()
            .loginId(loginId)
            .password(password)
            .name(name)
            .phone(phone)
            .role(MemberRole.USER)
            .status(MemberStatus.ACTIVE)
            .build();
    }

    public void changeMemberName(MemberName name) {
        this.name = Objects.requireNonNull(name);
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
}
