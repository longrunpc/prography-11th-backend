package com.longrunpc.api.admin.member.usecase;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.longrunpc.api.admin.member.dto.response.MemberDashboardResponse;
import com.longrunpc.api.admin.member.dto.response.MemberInfoResponse;
import com.longrunpc.common.constant.cohort.CohortConstants;
import com.longrunpc.api.admin.member.dto.request.MemberDashboardRequest;
import com.longrunpc.domain.cohort.repository.CohortMemberRepository;
import com.longrunpc.domain.cohort.entity.CohortMember;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetMemberDashboardUsecase {

    private final CohortMemberRepository cohortMemberRepository;
    
    @Transactional(readOnly = true)
    public MemberDashboardResponse execute(MemberDashboardRequest request) {
        int page = request.page() != null ? request.page() : CohortConstants.DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : CohortConstants.DEFAULT_SIZE;
        
        List<CohortMember> cohortMembers = cohortMemberRepository.findAllConditions(request.status(), request.searchType(), request.searchValue());

        if (request.generation() != null) {
            cohortMembers = cohortMembers.stream()
                .filter(cohortMember -> cohortMember.getCohort().getGeneration().getValue() == request.generation())
                .collect(Collectors.toList());
        }
        if (request.partName() != null) {
            cohortMembers = cohortMembers.stream()
                .filter(cohortMember -> cohortMember.getPart().getPartName().getValue().equals(request.partName()))
                .collect(Collectors.toList());
        }
        if (request.teamName() != null) {
            cohortMembers = cohortMembers.stream()
                .filter(cohortMember -> cohortMember.getTeam().getTeamName().getValue().equals(request.teamName()))
                .collect(Collectors.toList()); 
        }

        int totalElements = cohortMembers.size();
        int totalPages = calculateTotalPages(totalElements, size);

        List<MemberInfoResponse> memberInfoResponses = cohortMembers.stream()
            .skip(page * size)
            .limit(size)
            .map(MemberInfoResponse::of)
            .collect(Collectors.toList());

        return MemberDashboardResponse.of(memberInfoResponses, page, size, totalElements, totalPages);
    }

    private int calculateTotalPages(int totalElements, int size) {
        return (int) Math.ceil((double) totalElements / size);
    }
}
