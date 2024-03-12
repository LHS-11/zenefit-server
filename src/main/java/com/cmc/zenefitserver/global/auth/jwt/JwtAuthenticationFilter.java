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

        if (isAuthenticationAbsent()) {
            processJwtAuthentication(request);
        }

        filterChain.doFilter(request, response);
    }

    private void processJwtAuthentication(HttpServletRequest request) {
        String jwt = jwtService.getToken(request);
        if (StringUtils.isBlank(jwt)) {
            request.setAttribute("exception", ErrorCode.NOT_FOUND_TOKEN.getCode());
            return;
        }
        validateJwt(request, jwt);
    }

    private void validateJwt(HttpServletRequest request, String jwt) {
        try {
            if (jwtService.validateToken(jwt)) {
                authenticateUser(request, jwt);
            } else {
                log.info("Invalid JWT: {}", jwt);
                request.setAttribute("exception", ErrorCode.INVALID_AUTH_TOKEN.getCode());
            }
        } catch (Exception e) {
            request.setAttribute("exception", ErrorCode.JWT_PROCESSING_ERROR.getCode());
        }
    }

    private void authenticateUser(HttpServletRequest request, String jwt) {
        log.info("Valid JWT: {}", jwt);
        Authentication authentication = jwtService.getAuthentication(request, jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isAuthenticationAbsent() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

}
