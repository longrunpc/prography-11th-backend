package com.longrunpc.domain.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.longrunpc.domain.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySessionId(Long sessionId);

    List<Attendance> findBySessionIdIn(List<Long> sessionIds);
    
    boolean existsBySessionIdAndMemberId(Long sessionId, Long memberId);

    @Query("SELECT a FROM Attendance a " +
            "LEFT JOIN FETCH a.session " +
            "WHERE a.member.id = :memberId")
    List<Attendance> findAllWithSessionByMemberId(@Param("memberId") Long memberId);

    List<Attendance> findAllByMemberIdIn(List<Long> memberIds);
    
    List<Attendance> findAllByMemberId(Long memberId);
}
