package com.cmc.zenefitserver.global.config.feign.apple;

import feign.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppleFeignConfig {

    @Bean
    public Client appleFeignClient() {
        return new Client.Default(null, null);
    }

    @Bean
    public AppleFeignInterceptor appleFeignInterceptor() {
        return AppleFeignInterceptor.of();
    }

    @Bean
    public AppleFeignErrorDecoder appleFeignErrorDecoder() {
        return AppleFeignErrorDecoder.of();
    }
}
