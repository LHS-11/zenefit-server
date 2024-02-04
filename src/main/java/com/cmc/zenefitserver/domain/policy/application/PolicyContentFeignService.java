package com.cmc.zenefitserver.domain.policy.application;


import com.cmc.zenefitserver.domain.policy.dto.PolicyContentFeignDto;
import com.cmc.zenefitserver.global.config.feign.apple.AppleFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

@FeignClient(
        name = "policyContentClient",
        configuration = AppleFeignConfig.class
)
public interface PolicyContentFeignService {

    @PostMapping
    PolicyContentFeignDto getSupportContent(URI url, @RequestParam("text") String text);

//    List<PolicyContentFeignDto> getSupportContent(URI url, @RequestBody PolicyContentFeignRequestDto policyContentFeignRequestDto);
}
