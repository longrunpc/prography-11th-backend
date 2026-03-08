package com.longrunpc.api.admin.member.dto.request;

import com.longrunpc.domain.member.entity.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "멤버 대시보드 조회 조건")
public record MemberDashboardRequest(
    @Schema(description = "페이지 번호(0-base)", example = "0")
    Integer page,
    @Schema(description = "페이지 크기", example = "10")
    Integer size,
    @Schema(description = "검색 유형(name/loginId/phone)", example = "name")
    String searchType,
    @Schema(description = "검색어", example = "홍길동")
    String searchValue,
    @Schema(description = "기수", example = "11")
    Integer generation,
    @Schema(description = "파트명", example = "BACKEND")
    String partName,
    @Schema(description = "팀명", example = "A")
    String teamName,
    @Schema(description = "멤버 상태", example = "ACTIVE")
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