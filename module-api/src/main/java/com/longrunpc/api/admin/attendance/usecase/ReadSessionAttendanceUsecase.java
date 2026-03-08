package com.longrunpc.api.admin.attendance.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.attendance.dto.response.SessionAttendanceResponse;
import com.longrunpc.common.error.SessionErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.entity.Attendance;
import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.session.entity.Session;
import com.longrunpc.domain.session.repository.SessionRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadSessionAttendanceUsecase {
    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;

    @Transactional(readOnly = true)
    public SessionAttendanceResponse execute(Long sessionId) {

        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new BusinessException(SessionErrorCode.SESSION_NOT_FOUND));

        List<Attendance> attendances = attendanceRepository.findBySessionId(sessionId);
        
        return SessionAttendanceResponse.of(session, attendances);
    }
}
