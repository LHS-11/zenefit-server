package com.cmc.zenefitserver.global.config.feign.apple;

import com.cmc.zenefitserver.global.config.feign.kakao.KakaoFeignErrorDecoder;
import com.cmc.zenefitserver.global.config.feign.kakao.KakaoFeignInterceptor;
import feign.Client;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"com.cmc"})
public class AppleFeignConfig {

    @Bean
    public Client feignClient() {
        return new Client.Default(null, null);
    }

    @Bean
    public KakaoFeignInterceptor feignInterceptor(){
        return KakaoFeignInterceptor.of();
    }

    @Bean
    public KakaoFeignErrorDecoder feignErrorDecoder(){
        return KakaoFeignErrorDecoder.of();
    }
}
