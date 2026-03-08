package com.longrunpc.domain.session.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.entity.SessionStatus;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT s FROM Session s WHERE s.cohort.id = :cohortId AND s.sessionStatus != :status")
    List<Session> findByCohortIdAndSessionStatusNot(@Param("cohortId") Long cohortId, @Param("status") SessionStatus status);

    @Query("SELECT s FROM Session s WHERE s.cohort.id = :cohortId AND s.sessionDate BETWEEN :dateFrom AND :dateTo AND s.sessionStatus = :sessionStatus")
    List<Session> findByCohortIdAndSessionDateBetweenAndSessionStatus(@Param("cohortId") Long cohortId, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("sessionStatus") SessionStatus sessionStatus);
}
