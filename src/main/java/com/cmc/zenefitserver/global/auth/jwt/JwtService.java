package com.cmc.zenefitserver.global.auth.jwt;

import com.cmc.zenefitserver.global.common.request.TokenRequestDto;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import com.cmc.zenefitserver.global.error.ErrorCode;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    /**
     * 토큰 유효 시간 (ms)
     */
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7; // 임시설정 유효시간 7일

    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 30 * 24; //임시설정 유효시간 31일

    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String TOKEN_HEADER_NAME = "Authorization";

    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    /**
     * request header 에서 토큰 추출
     *
     * @param request
     * @return
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER_NAME);

        if (StringUtils.isNotBlank(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return token;
    }

    /**
     * login 할 때 id를 받아서 토큰 생성
     *
     * @param tokenRequestDto
     * @return
     */
    public TokenResponseDto createToken(TokenRequestDto tokenRequestDto) {
        final String accessToken = generateAccessToken(tokenRequestDto);
        final String refreshToken = generateRefreshToken(tokenRequestDto);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Access Token 생성
     *
     * @param tokenRequestDto
     * @return
     */
    public String generateAccessToken(TokenRequestDto tokenRequestDto) {

        final String encodedKey = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes());
        final Date now = new Date();
        final Date accessTokenExpiresIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

        final String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("zenefit")
                .setIssuedAt(now)
                .setSubject(tokenRequestDto.getId().toString())
                .setExpiration(accessTokenExpiresIn)
                .claim("id", tokenRequestDto.getId())
                .signWith(SignatureAlgorithm.HS256, encodedKey)
                .compact();

        return accessToken;
    }

    /**
     * Refresh Token 생성
     *
     * @param tokenRequestDto
     * @return
     */
    // Todo : redis 에 refresh token 등록
    public String generateRefreshToken(TokenRequestDto tokenRequestDto) {
        final String encodedKey = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes());
        final Date now = new Date();
        final Date refreshTokenExpiresIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

        final String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("zenefit")
                .setExpiration(refreshTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS512, encodedKey)
                .compact();

        // redis 에 리프레시 토큰 등록

        return refreshToken;
    }

    /**
     * Security Session 추출
     *
     * @param request
     * @param accessToken
     * @return
     */
    public Authentication getAuthentication(HttpServletRequest request, String accessToken) {
        Claims claims = parseClaims(accessToken);

        try {
            UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());
            return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        } catch (Exception e) {
            request.setAttribute("exception", ErrorCode.INVALID_USER.getCode());
            throw e;
        }
    }

    /**
     * 토큰 유효성 체크
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        final String encodedKey = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes());
        try {
            Jwts.parser().setSigningKey(encodedKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 토큰입니다");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 비어있습니다");
        }
        return false;
    }

    /**
     * JWT 클레임 추출
     *
     * @param accessToken
     * @return
     */
    public Claims parseClaims(String accessToken) {
        final String encodedKey = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes());

        try {
            return Jwts.parser()
                    .setSigningKey(encodedKey)
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}