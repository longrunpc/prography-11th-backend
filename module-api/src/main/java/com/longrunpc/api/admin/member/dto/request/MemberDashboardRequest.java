package com.longrunpc.api.admin.member.dto.request;

import com.longrunpc.domain.member.entity.MemberStatus;

public record MemberDashboardRequest(
    Integer page,
    Integer size,
    String searchType,
    String searchValue,
    Integer generation,
    String partName,
    String teamName,
    MemberStatus status
) {
}

// page	Int	0	X	페이지 번호 (0-based)
// size	Int	10	X	페이지 크기
// searchType	String	-	X	검색 유형: name, loginId, phone
// searchValue	String	-	X	검색어
// generation	Int	-	X	기수 필터
// partName	String	-	X	파트명 필터
// teamName	String	-	X	팀명 필터
// status	MemberStatus	-	X	상태 필터 (ACTIVE, INACTIVE, WITHDRAWN)