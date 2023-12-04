package com.cmc.zenefitserver.domain.policy.api;

import com.cmc.zenefitserver.domain.policy.application.PolicyService;
import com.cmc.zenefitserver.domain.policy.dto.*;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.annotation.AuthUser;
import com.cmc.zenefitserver.global.common.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Api(tags = "3. policy", description = "policy API")
@RequiredArgsConstructor
@RequestMapping("/policy")
@RestController
public class PolicyController {

    private final PolicyService policyService;

    // 정책 리스트 조회 API
    @GetMapping
    @Operation(summary = "정책 목록 조회 API", description = "정책 목록을 무한 스크롤해서 보여줍니다.")
    public CommonResponse<Page<PolicyListResponseDto>> getPolices(@AuthUser User user, @RequestBody PolicyListRequestDto policyListRequestDto, @RequestParam int page, @RequestParam int size, @RequestParam String sortField,
                                                                  @RequestParam String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
        return CommonResponse.success(policyService.getPolicyList(user, policyListRequestDto, page, size, sort));
    }

    // 정책 검색 API
    @GetMapping("/search")
    @Operation(summary = "정책 검색 API", description = "키워드를 사용하여 해당하는 정책 목록을 무한 스크롤해서 보여줍니다.")
    public CommonResponse<Slice<PolicyListResponseDto>> getSearchPolices(@AuthUser User user, @RequestBody SearchPolicyListRequestDto policyListRequestDto, @RequestParam int page, @RequestParam int size, @RequestParam String sortField,
                                                                         @RequestParam String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortField);
        return CommonResponse.success(policyService.getSearchPolicyList(user, policyListRequestDto, page, size, sort));
    }

    // 정책 상세 조회 API
    @GetMapping("/{policyId}")
    @Operation(summary = "정책 상세 조회 API", description = "해당하는 정책을 상세 조회합니다.")
    public CommonResponse<PolicyInfoResponseDto> getPolicy(@AuthUser User user, @PathVariable Long policyId) {
        log.info("policyId = {}", policyId);
        return CommonResponse.success(policyService.getPolicy(user, policyId));
    }

    @GetMapping("/calendar")
    @Operation(summary = "특정 날짜에 따른 관심 정책 조회 API", description = "달력에서 해당 날짜(달)에 신청 시작일 또는 신청 종료일을 가지는 정책을 조회합니다.")
    public CommonResponse<List<CalendarPolicyListResponseDto>> getPolicesBySearchDate(@AuthUser User user, @RequestParam("searchDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate searchDate, @RequestParam String searchDateType) {
        List<CalendarPolicyListResponseDto> result = policyService.getPolicyListBySearchDate(user, searchDate, searchDateType);
        return CommonResponse.success(result);
    }

    @GetMapping("/recommend")
    @Operation(summary = "지원 정책 유형에 따른 추천 정책 API", description = "로그인(회원가입)하고, 해당 유저에게 정책 카테고리를 클릭했을 때 정책 유형에 따른 정책을 추천합니다.")
    public CommonResponse<RecommendPolicyInfoResponseDto> recommend(@AuthUser User user) {
        RecommendPolicyInfoResponseDto result = policyService.getRecommendPolicyDummy();
        return CommonResponse.success(result);
    }

    @GetMapping("/recommend/count")
    @Operation(summary = "지원할 수 있는 정책의 수 조회 API", description = "로그인시 해당 유저가 지원할 수 있는 정책의 수를 가져옵니다.")
    public CommonResponse<PolicyCountResponseDto> getRecommend(@AuthUser User user) {
        PolicyCountResponseDto result = policyService.getRecommendCountAndNickname(user);
        return CommonResponse.success(result);
    }
}
