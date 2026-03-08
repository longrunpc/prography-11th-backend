package com.longrunpc.domain.session.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

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
    private Session(Long id, Cohort cohort, SessionTitle title, LocalDate sessionDate, LocalTime sessionTime, SessionLocation sessionLocation, SessionStatus sessionStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.cohort = Objects.requireNonNull(cohort);
        this.title = Objects.requireNonNull(title);
        this.sessionDate = Objects.requireNonNull(sessionDate);
        this.sessionTime = Objects.requireNonNull(sessionTime);
        this.sessionLocation = Objects.requireNonNull(sessionLocation);
        this.sessionStatus = Objects.requireNonNull(sessionStatus);
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
        if (sessionDate == null || sessionTime == null) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT);
        }
        if (LocalDateTime.of(sessionDate, sessionTime).isBefore(LocalDateTime.now())) {
            throw new BusinessException(GlobalErrorCode.INVALID_INPUT);
        }
    }

    public void changeTitle(SessionTitle title) {
        this.title = Objects.requireNonNull(title);
    }

    public void changeSessionDate(LocalDate sessionDate) {
        this.sessionDate = Objects.requireNonNull(sessionDate);
    }

    public void changeSessionTime(LocalTime sessionTime) {
        this.sessionTime = Objects.requireNonNull(sessionTime);
    }
    
    public void changeSessionLocation(SessionLocation sessionLocation) {
        this.sessionLocation = Objects.requireNonNull(sessionLocation);
    }

    public void changeSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = Objects.requireNonNull(sessionStatus);
    }

    public void cancel() {
        this.sessionStatus = SessionStatus.CANCELLED;
    }

    public boolean isCancelled() {
        return this.sessionStatus == SessionStatus.CANCELLED;
    }

    public boolean isInProgress() {
        return this.sessionStatus == SessionStatus.IN_PROGRESS;
    }
}

