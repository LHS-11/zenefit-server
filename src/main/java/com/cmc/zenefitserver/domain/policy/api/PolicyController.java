package com.cmc.zenefitserver.domain.policy.api;

import com.cmc.zenefitserver.domain.policy.application.PolicyClassifier;
import com.cmc.zenefitserver.domain.policy.application.PolicyService;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.dto.PolicyListRequestDto;
import com.cmc.zenefitserver.domain.policy.dto.PolicyListResponseDto;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.annotation.AuthUser;
import com.cmc.zenefitserver.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/policy")
@RestController
public class PolicyController {

    private final PolicyService policyService;

    // 정책 리스트 조회 API
    @GetMapping
    public CommonResponse<Slice<PolicyListResponseDto>> getPolicyList(@AuthUser User user, @RequestBody PolicyListRequestDto policyListRequestDto, Pageable pageable){
        System.out.println("pageable = " + pageable);
        return CommonResponse.success(policyService.getPolicyList(user,policyListRequestDto,pageable));
    }

    // 정책 상세 조회 API

    // 정책 검색 API

}
