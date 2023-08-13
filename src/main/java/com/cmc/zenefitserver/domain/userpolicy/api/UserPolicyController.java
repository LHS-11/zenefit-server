package com.cmc.zenefitserver.domain.userpolicy.api;

import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.userpolicy.application.UserPolicyService;
import com.cmc.zenefitserver.domain.userpolicy.dto.ApplyPolicyListResponseDto;
import com.cmc.zenefitserver.domain.userpolicy.dto.InterestPolicyListResponseDto;
import com.cmc.zenefitserver.global.annotation.AuthUser;
import com.cmc.zenefitserver.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RequestMapping("/user/policy")
@RestController
public class UserPolicyController {

    private final UserPolicyService userPolicyService;

    @PostMapping("{policyId}")
    public CommonResponse<String> saveInterestPolicy(@AuthUser User user, @PathVariable Long policyId){
        userPolicyService.saveInterestPolicy(user,policyId);
        return CommonResponse.success(null);
    }

    @PostMapping("/apply/{policyId}")
    public CommonResponse<String> saveApplyPolicy(@AuthUser User user,@PathVariable Long policyId){
        userPolicyService.saveApplyPolicy(user, policyId);
        return CommonResponse.success(null);
    }

    @GetMapping
    public CommonResponse<List<InterestPolicyListResponseDto>> getInterestPolices(@AuthUser User user){
        List<InterestPolicyListResponseDto> result= userPolicyService.getInterestPolicyList(user);
        return CommonResponse.success(result);
    }

    @GetMapping("/apply")
    public CommonResponse<List<ApplyPolicyListResponseDto>> getApplyPolices(@AuthUser User user){
        List<ApplyPolicyListResponseDto> result= userPolicyService.getApplyPolicyList(user);
        return CommonResponse.success(result);
    }

    @DeleteMapping("{policyId}")
    public CommonResponse<String> deleteInterestPolicy(@AuthUser User user,@PathVariable Long policyId){
        userPolicyService.deleteInterestPolicy(user, policyId);
        return CommonResponse.success(null);
    }
}
