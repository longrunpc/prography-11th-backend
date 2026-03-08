package com.longrunpc.api.user.attendance.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.longrunpc.domain.attendance.repository.AttendanceRepository;
import com.longrunpc.domain.member.repository.MemberRepository;

import java.util.List;

import com.longrunpc.api.user.attendance.dto.response.AttendanceResponse;
import com.longrunpc.common.error.MemberErrorCode;
import com.longrunpc.common.exception.BusinessException;
import com.longrunpc.domain.attendance.entity.Attendance;

@Service
@RequiredArgsConstructor
public class ReadAttendancesUsecase {
    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<AttendanceResponse> execute(Long memberId) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepository.findAllWithSessionByMemberId(memberId);
        return attendances.stream().map(AttendanceResponse::of).toList();
    }
}
