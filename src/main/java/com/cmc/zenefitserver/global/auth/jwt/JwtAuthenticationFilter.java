package com.cmc.zenefitserver.global.auth.jwt;

import com.cmc.zenefitserver.global.error.ErrorCode;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(SecurityContextHolder.getContext().getAuthentication()==null){
            try{
                String jwt= jwtService.getToken(request);
                log.info("JWT : {}",jwt);
                if(jwt==null){
                    request.setAttribute("exception", ErrorCode.NOT_FOUND_TOKEN.getCode());
                    filterChain.doFilter(request, response);
                    return;
                }

                if(StringUtils.isNotBlank(jwt) && jwtService.validateToken(jwt)){
                    Authentication authentication= jwtService.getAuthentication(request,jwt);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                if(StringUtils.isNotBlank(jwt)){
                    request.setAttribute("exception", ErrorCode.NOT_FOUND_TOKEN.getCode());
                    filterChain.doFilter(request, response);
                    return;
                }
            }catch (Exception e){
                log.error("Security Context 에 해당 토큰을 등록할 수 없습니다", e);
            }
        }

        filterChain.doFilter(request,response);
    }

}
