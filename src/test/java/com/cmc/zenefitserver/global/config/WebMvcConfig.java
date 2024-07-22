package com.cmc.zenefitserver.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthUserArgumentResolver authUserArgumentResolver;

    public WebMvcConfig(AuthUserArgumentResolver authUserArgumentResolver){
        this.authUserArgumentResolver = authUserArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authUserArgumentResolver);
    }
}
