package com.longrunpc.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import com.longrunpc.domain.common.entity.BaseEntity;
import com.longrunpc.domain.member.vo.LoginId;
import com.longrunpc.domain.member.vo.MemberName;
import com.longrunpc.domain.member.vo.Password;
import com.longrunpc.domain.member.vo.Phone;

@Entity
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "login_id", nullable = false)
    private LoginId loginId;

    @Column(name = "password", nullable = false)
    private Password password;

    @Column(name = "name", nullable = false)
    private MemberName name;
    
    @Column(name = "phone", nullable = false)
    private Phone phone;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    public void changeMemberName(MemberName name) {
        this.name = name;
    }

    public void changePhone(Phone phone) {
        this.phone = phone;
    }
}
