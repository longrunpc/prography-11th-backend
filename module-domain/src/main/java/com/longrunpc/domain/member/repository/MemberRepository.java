package com.longrunpc.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.longrunpc.domain.member.entity.Member;
import com.longrunpc.domain.member.vo.LoginId;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(LoginId loginId);
}
