package com.cmc.zenefitserver.global.config.feign.kakao;

import feign.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KakaoFeignConfig {

    @Bean
    public Client kakaoFeignClient() {
        return new Client.Default(null, null);
    }

    @Bean
    public KakaoFeignInterceptor kakaoFeignInterceptor() {
        return KakaoFeignInterceptor.of();
    }

    @Bean
    public KakaoFeignErrorDecoder kakaoFeignErrorDecoder() {
        return KakaoFeignErrorDecoder.of();
    }
}
