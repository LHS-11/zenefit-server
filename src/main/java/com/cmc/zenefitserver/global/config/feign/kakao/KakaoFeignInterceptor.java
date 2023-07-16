package com.cmc.zenefitserver.global.config.feign.kakao;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

@Slf4j
@RequiredArgsConstructor(staticName = "of")
public final class KakaoFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        if(template.method()== HttpMethod.GET.name()){
            log.info("[GET] Path {}",template.path());
        }
        return;
    }
}
