package com.cmc.zenefitserver.global.auth.kakao;

import com.cmc.zenefitserver.global.config.feign.kakao.KakaoFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

@Component
@FeignClient(
        name = "KakaoFeignService",
        configuration = KakaoFeignConfig.class
)
public interface KakaoFeignService {


    @PostMapping
    KakaoInfo getInfo(URI baseUrl, @RequestHeader("Authorization") String accessToken);

    @PostMapping
    KakaoToken getToken(URI baseUrl, @RequestParam("grant_type") String grantType,
                        @RequestParam("client_id") String restApiKey,
                        @RequestParam("redirect_uri") String redirectUrl,
                        @RequestParam("code") String code);

}
