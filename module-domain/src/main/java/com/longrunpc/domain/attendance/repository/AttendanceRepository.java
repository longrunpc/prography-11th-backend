package com.longrunpc.domain.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.longrunpc.domain.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySessionId(Long sessionId);

    List<Attendance> findBySessionIdIn(List<Long> sessionIds);

    boolean existsBySessionIdAndMemberId(Long sessionId, Long memberId);
}
