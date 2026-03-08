package com.longrunpc.api.admin.session.usecase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.session.dto.request.ReadSessionDetailsRequest;
import com.longrunpc.api.admin.session.dto.response.SessionDetailResponse;
import com.longrunpc.common.error.CohortErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.cohort.entity.Cohort;
import com.longrunpc.domain.cohort.repository.CohortRepository;
import com.longrunpc.domain.cohort.vo.Generation;
import com.longrunpc.domain.session.entity.QrCode;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.repository.QrCodeRepository;
import com.longrunpc.domain.session.repository.SessionRepository;
import com.longrunpc.domain.attendance.entity.Attendance;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadSessionDetailsUsecase {

    @Value("${prography.current-cohort.generation}")
    private int currentGeneration;

    private final SessionRepository sessionRepository;
    private final CohortRepository cohortRepository;
    private final AttendanceRepository attendanceRepository;
    private final QrCodeRepository qrCodeRepository;

    @Transactional(readOnly = true)
    public List<SessionDetailResponse> execute(ReadSessionDetailsRequest request) {
        Cohort cohort = cohortRepository.findByGeneration(new Generation(currentGeneration))
            .orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));

        List<Session> sessions = sessionRepository.findByCohortIdAndSessionDateBetweenAndSessionStatus(cohort.getId(), request.dateFrom(), request.dateTo(), request.sessionStatus());

        List<Attendance> attendances = attendanceRepository.findBySessionIdIn(sessions.stream().map(Session::getId).toList());
        LocalDateTime now = LocalDateTime.now();
        List<QrCode> qrCodes = qrCodeRepository.findBySessionIdInAndExpiresAtAfter(sessions.stream().map(Session::getId).toList(), now);
        
        Map<Long, List<Attendance>> attendanceMap = attendances.stream().collect(Collectors.groupingBy(a -> a.getSession().getId()));
        Map<Long, List<QrCode>> qrCodeMap = qrCodes.stream().collect(Collectors.groupingBy(q -> q.getSession().getId()));

        return sessions.stream().map(session -> SessionDetailResponse.of(
            session, 
            attendanceMap.get(session.getId()),
            qrCodeMap.containsKey(session.getId())
        )).toList();
    }
    
}
