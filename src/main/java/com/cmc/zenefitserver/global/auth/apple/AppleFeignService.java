package com.cmc.zenefitserver.global.auth.apple;

import com.cmc.zenefitserver.global.config.feign.apple.AppleFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

@FeignClient(
        name="appleClient",
        configuration= AppleFeignConfig.class
)
public interface AppleFeignService {

    @GetMapping
    AppleKeyInfo getAppleKeys(URI baseURI);
}
