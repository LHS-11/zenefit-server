package com.cmc.zenefitserver.global.auth.kakao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class KakaoLoginService {

    private final KakaoFeignService kakaoFeignService;

    @Value("${kakao.auth-url}")
    private String kakaoAuthUrl;

    @Value("${kakao.user-api-url}")
    private String kakaoUserApiUrl;

    @Value("${kakao.restapi-key}")
    private String restapiKey;

    @Value("${kakao.redirect-url}")
    private String redirectUrl;

    public KakaoInfo getInfo(final String code){

        // code -> access token 획득
        try {
            KakaoToken token = getToken(code);
            log.debug("token = {}", token);

            // access token -> 유저 정보 획득
            return kakaoFeignService.getInfo(new URI(kakaoUserApiUrl), token.getTokenType() + " " + token.getAccessToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private KakaoToken getToken(String code) throws URISyntaxException {
        return kakaoFeignService.getToken(new URI(kakaoAuthUrl), "authorization_code", restapiKey, redirectUrl, code);
    }
}
