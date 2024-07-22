package com.cmc.zenefitserver.global.auth;

import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.domain.user.domain.Gender;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.user.domain.UserDetail;
import com.cmc.zenefitserver.global.auth.apple.AppleFeignService;
import com.cmc.zenefitserver.global.auth.apple.AppleKeyInfo;
import com.cmc.zenefitserver.global.auth.jwt.JwtService;
import com.cmc.zenefitserver.global.auth.kakao.KakaoAccount;
import com.cmc.zenefitserver.global.auth.kakao.KakaoLoginService;
import com.cmc.zenefitserver.global.common.request.AuthRequestDto;
import com.cmc.zenefitserver.global.common.request.TokenRequestDto;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import com.cmc.zenefitserver.global.error.exception.NoAuthException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

import static com.cmc.zenefitserver.global.error.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private static final String APPLE_REQUEST_URL = "https://appleid.apple.com";
    private static final String ZENEFIT_ISSUER = "iosdev.sw.ZeneFitApp";

    private final UserRepository userRepository;
    private final KakaoLoginService kakaoLoginService;
    private final AppleFeignService appleFeignService;
    private final JwtService jwtService;
    private final EntityManager em;

    public TokenResponseDto login(AuthRequestDto authRequestDto) {
        ProviderType providerType = authRequestDto.getProviderType();
        if(providerType == ProviderType.KAKAO){
            return kakaoLogin(authRequestDto);
        }
        if (providerType == ProviderType.APPLE){
            return appleLogin(authRequestDto);
        }
        return null;
    }

    @Transactional(noRollbackFor = NoAuthException.class)
    public TokenResponseDto kakaoLogin(AuthRequestDto authRequestDto) {

        KakaoAccount kakaoAccount = kakaoLoginService.getInfo(authRequestDto.getToken()).getKakaoAccount();
        TokenResponseDto jwtToken = getTokenResponseDto(authRequestDto, kakaoAccount.getEmail(), kakaoAccount.getProfile().getNickname(), kakaoAccount.getGender(), authRequestDto.getProviderType());
        return jwtToken;
    }

    @Transactional(noRollbackFor = NoAuthException.class)
    public TokenResponseDto appleLogin(AuthRequestDto authRequestDto) {
        String token = authRequestDto.getToken().replaceAll("–", "--");
        JsonParser parser = new JsonParser();

        // 통신 결과에서 공개키 목록 가져오기
        AppleKeyInfo appleKeys = appleFeignService.getAppleKeys(URI.create("https://appleid.apple.com/auth/keys"));
        List<AppleKeyInfo.Key> keys = appleKeys.getKeys();

        // 애플의 공개키 3개 중 클라이언트 토큰과 kid, alg 일치하는 것 찾기
        String headerOfIdentityToken = token.substring(0, token.indexOf("."));
        String header = new String(Base64.getDecoder().decode(headerOfIdentityToken), StandardCharsets.UTF_8);
//        JsonObject parseHeader = JsonParser.parseString(header).getAsJsonObject();
        JsonObject parseHeader = (new Gson()).fromJson(header, JsonObject.class);
        String kidValue = parseHeader.get("kid").getAsString();
        String algValue = parseHeader.get("alg").getAsString();

        AppleKeyInfo.Key foundKey = keys.stream()
                .filter(key -> kidValue.equals(key.getKid()) && algValue.equals(key.getAlg()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(MISMATCH_APPLE_KEY));

        PublicKey publicKey = getPublicKey(foundKey);

        // 일치하는 키를 이용해 정보 확인 후, 사용자 정보 가져오기
        Claims userInfo = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
        JsonObject userInfoObject = (JsonObject) parser.parse(new Gson().toJson(userInfo));
        String iss = userInfoObject.get("iss").getAsString();
        String aud = userInfoObject.get("aud").getAsString();

        if (!Objects.equals(iss, APPLE_REQUEST_URL)) {
            throw new BusinessException(MISMATCH_ISSUER);
        }

        if (!Objects.equals(aud, ZENEFIT_ISSUER)) {
            throw new BusinessException(MISMATCH_AUDIENCE);
        }

        String email = userInfoObject.get("email").getAsString();

        TokenResponseDto jwtToken = getTokenResponseDto(authRequestDto, email, authRequestDto.getNickname(), null, authRequestDto.getProviderType());
        return jwtToken;
    }

    public PublicKey getPublicKey(AppleKeyInfo.Key key) {
        String nStr = key.getN();
        String eStr = key.getE();

        byte[] nBytes = Base64.getUrlDecoder().decode(nStr.substring(0));
        byte[] eBytes = Base64.getUrlDecoder().decode(eStr.substring(0));

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        try {
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(publicKeySpec);
        } catch (Exception exception) {
            throw new BusinessException(FAIL_TO_MAKE_APPLE_PUBLIC_KEY);
        }
    }

    private TokenResponseDto getTokenResponseDto(AuthRequestDto authRequestDto, String email, String nickname, String gender, ProviderType providerType) {
        // 이메일과 프로바이더로 회원조회
        User findUser = null;
        try {
            findUser = userRepository.findByEmailAndProvider(email, providerType)
                    .orElseThrow(() -> new NoAuthException(NOT_FOUND_USER, new HashMap<>()));

        } catch (BusinessException exception) {

            // 1. DB에 정보가 없을 때, 해당 정보로 임시 회원가입 진행하고 NOT_FOUND_USER 2001 예외 처리
            UserDetail userDetail = UserDetail.builder()
                    .gender(gender == null ? Gender.MALE : Gender.fromStringToName(gender))
                    .build();

            User user = User.builder()
                    .email(email)
                    .nickname(nickname == null ? RandomStringUtils.random(10, true, true) : nickname)
                    .provider(authRequestDto.getProviderType())
                    .userDetail(userDetail)
                    .build();

            userDetail.setUser(user);
            User savedUser = userRepository.save(user);
            em.flush();
            em.clear();

            exception.setData(savedUser);
            throw exception;
        }

        // 2. 이미 임시 회원가입이 되어 있을 경우, INVALID_USER 2005 예외 처리
        if (!findUser.isUserRegistrationValid()) {
            throw new NoAuthException(INVALID_USER, Map.of("userId", findUser.getUserId().toString()));
        }

        // 3. 이메일로 회원 조회시 있으면 로그인 하고 자체 JWT 만들어서 Access Token 과 Refresh Token 반환
        TokenResponseDto jwtToken = jwtService.createToken(new TokenRequestDto(findUser));
        return jwtToken;
    }

}
