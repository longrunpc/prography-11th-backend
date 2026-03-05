package com.longrunpc.domain.session.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.cohort.entity.Cohort;

import com.longrunpc.domain.common.entity.BaseEntity;
import com.longrunpc.domain.session.vo.SessionLocation;
import com.longrunpc.domain.session.vo.SessionTitle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;


@Entity
@Table(name = "session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    @Embedded
    private SessionTitle title;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "session_time", nullable = false)
    private LocalTime sessionTime;
    
    @Embedded
    private SessionLocation sessionLocation;

    @Column(name = "session_status", nullable = false)
    private SessionStatus sessionStatus;

    @Builder
    private Session(Long id, Cohort cohort, SessionTitle title, LocalDate sessionDate, LocalTime sessionTime, SessionLocation sessionLocation, SessionStatus sessionStatus) {
        this.id = id;
        this.cohort = cohort;
        this.title = title;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.sessionLocation = sessionLocation;
        this.sessionStatus = sessionStatus;
    }

    public static Session createSession(Cohort cohort, SessionTitle title, LocalDate sessionDate, LocalTime sessionTime, SessionLocation sessionLocation) {
        validate(sessionDate, sessionTime);
        return Session.builder()
            .cohort(cohort)
            .title(title)
            .sessionDate(sessionDate)
            .sessionTime(sessionTime)
            .sessionLocation(sessionLocation)
            .sessionStatus(SessionStatus.SCHEDULED)
            .build();
    }

    private static void validate(LocalDate sessionDate, LocalTime sessionTime) {
        if (LocalDateTime.of(sessionDate, sessionTime).isBefore(LocalDateTime.now())) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT);
        }
    }
}

