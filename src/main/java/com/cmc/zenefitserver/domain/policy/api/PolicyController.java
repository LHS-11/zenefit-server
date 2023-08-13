package com.cmc.zenefitserver.domain.policy.api;

import com.cmc.zenefitserver.domain.policy.application.PolicyService;
import com.cmc.zenefitserver.domain.policy.dto.PolicyInfoResponseDto;
import com.cmc.zenefitserver.domain.policy.dto.PolicyListRequestDto;
import com.cmc.zenefitserver.domain.policy.dto.PolicyListResponseDto;
import com.cmc.zenefitserver.domain.policy.dto.SearchPolicyListRequestDto;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.annotation.AuthUser;
import com.cmc.zenefitserver.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/policy")
@RestController
public class PolicyController {

    private final PolicyService policyService;

    // 정책 리스트 조회 API
    @GetMapping
    public CommonResponse<Slice<PolicyListResponseDto>> getPolices(@AuthUser User user, @RequestBody PolicyListRequestDto policyListRequestDto, Pageable pageable){
        log.info("pageable = {}", pageable);
        return CommonResponse.success(policyService.getPolicyList(user,policyListRequestDto,pageable));
    }

    // 정책 검색 API
    @GetMapping("/search")
    public CommonResponse<Slice<PolicyListResponseDto>> getSearchPolices(@AuthUser User user, @RequestBody SearchPolicyListRequestDto policyListRequestDto, Pageable pageable){
        log.info("pageable = {}", pageable);
        return CommonResponse.success(policyService.getSearchPolicyList(user,policyListRequestDto,pageable));
    }

    // 정책 상세 조회 API
    @GetMapping("/{policyId}")
    public CommonResponse<PolicyInfoResponseDto> getPolicy(@AuthUser User user, @PathVariable Long policyId){
        log.info("policyId = {}",policyId);
        return CommonResponse.success(policyService.getPolicy(user, policyId));
    }
}
