package com.longrunpc.domain.session.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.time.LocalDate;
import java.time.LocalTime;

import com.longrunpc.domain.cohort.entity.Cohort;

import com.longrunpc.domain.common.entity.BaseEntity;

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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "session_time", nullable = false)
    private LocalTime sessionTime;
    
    @Column(name = "session_location", nullable = false)
    private String sessionLocation;

    @Column(name = "session_status", nullable = false)
    private SessionStatus sessionStatus;

    @Builder
    private Session(Long id, Cohort cohort, String title, LocalDate sessionDate, LocalTime sessionTime, String sessionLocation, SessionStatus sessionStatus) {
        this.id = id;
        this.cohort = cohort;
        this.title = title;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.sessionLocation = sessionLocation;
        this.sessionStatus = sessionStatus;
    }

    public static Session createSession(Cohort cohort, String title, LocalDate sessionDate, LocalTime sessionTime, String sessionLocation) {
        return Session.builder()
            .cohort(cohort)
            .title(title)
            .sessionDate(sessionDate)
            .sessionTime(sessionTime)
            .sessionLocation(sessionLocation)
            .sessionStatus(SessionStatus.SCHEDULED)
            .build();
    }
}

