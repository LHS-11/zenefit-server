package com.cmc.zenefitserver.domain.userpolicy.api;

import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.userpolicy.application.UserPolicyService;
import com.cmc.zenefitserver.global.annotation.AuthUser;
import com.cmc.zenefitserver.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
