package com.cmc.zenefitserver.global.config.security;

import com.cmc.zenefitserver.global.auth.jwt.JwtAccessDeniedHandler;
import com.cmc.zenefitserver.global.auth.jwt.JwtAuthenticationEntryPoint;
import com.cmc.zenefitserver.global.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers(
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/swagger-ui/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin().disable() // 폼 로그인 비활성화
                .httpBasic().disable() // HTTP 기본 인증 비활성화
                .csrf().disable() // CSRF 비활성화
                .cors().configurationSource(corsConfigurationSource()) // CORS 설정 활성화
                .and()
                .exceptionHandling() // 예외 처리 핸들러 설정
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 진입점을 설정
                .accessDeniedHandler(jwtAccessDeniedHandler) // 권한이 없는 사용자 접근 거부 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT 를 사용할 것이기 때문에 세션 사용 X
                .and()
                //권한이 필요한 요청에 대한 설정
                .authorizeRequests()
//                .antMatchers("/test/**").permitAll()
                .antMatchers("/policy/**").permitAll()
                .antMatchers("/user/signup").permitAll()
                .antMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
//                .headers().frameOptions().sameOrigin() // 같은 출처의 페이지만 현재 페이지를 프레임으로 사용할 수 있게 허용하는 설정 ( clickjacking 방지 )
//                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
