package com.longrunpc.api.admin.cohort.dto.response;

import java.time.LocalDateTime;

import com.longrunpc.domain.cohort.entity.DepositHistory;
import com.longrunpc.domain.cohort.entity.DepositType;

import lombok.Builder;

@Builder
public record CohortMemberDepositHistoryResponse(
    Long id,
    Long cohortMemberId,
    DepositType depositType,
    int amount,
    int balanceAfter,
    Long attendanceId,
    String description,
    LocalDateTime createdAt
) {
    public static CohortMemberDepositHistoryResponse of(DepositHistory depositHistory) {
        return CohortMemberDepositHistoryResponse.builder()
            .id(depositHistory.getId())
            .cohortMemberId(depositHistory.getCohortMember().getId())
            .depositType(depositHistory.getDepositType())
            .amount(depositHistory.getAmount())
            .balanceAfter(depositHistory.getBalanceAfter())
            .attendanceId(depositHistory.getAttendance() != null ? depositHistory.getAttendance().getId() : null)
            .description(depositHistory.getDescription().getValue())
            .createdAt(depositHistory.getCreatedAt())
            .build();
    }
}
