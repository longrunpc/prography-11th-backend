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

@Entity
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;
}
