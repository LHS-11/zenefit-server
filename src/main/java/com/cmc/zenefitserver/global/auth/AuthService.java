package com.cmc.zenefitserver.global.auth;

import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.auth.apple.AppleFeignService;
import com.cmc.zenefitserver.global.auth.apple.AppleKeyInfo;
import com.cmc.zenefitserver.global.auth.jwt.JwtService;
import com.cmc.zenefitserver.global.auth.kakao.KakaoAccount;
import com.cmc.zenefitserver.global.auth.kakao.KakaoLoginService;
import com.cmc.zenefitserver.global.common.request.AuthRequestDto;
import com.cmc.zenefitserver.global.common.request.TokenRequestDto;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.cmc.zenefitserver.global.error.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class AuthService {

    private static final String APPLE_REQUEST_URL = "https://appleid.apple.com";
    private static final String ZENEFIT_ISSUER = "teamzenefit.zenefit";

    private final UserRepository userRepository;
    private final KakaoLoginService kakaoLoginService;
    private final AppleFeignService appleFeignService;
    private final JwtService jwtService;

    public TokenResponseDto kakaoLogin(AuthRequestDto authRequestDto) {

//        String code = authRequestDto.getToken();
        KakaoAccount kakaoAccount = kakaoLoginService.getInfo(authRequestDto.getToken()).getKakaoAccount();

        // 이메일로 회원 조회시 없으면 이메일과 함께 오류 반환
        User findUser = userRepository.findByEmailAndProvider(kakaoAccount.getEmail(), ProviderType.KAKAO)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER,
                        Map.of("email", kakaoAccount.getEmail(),
                                "gender", kakaoAccount.getGender(),
                                "provider", "KAKAO")
                ));

        // 이메일로 회원 조회시 있으면 로그인하고 자체 JWT 만들어서 Access Token 과 Refresh Token 반환
        TokenResponseDto jwtToken = jwtService.createToken(new TokenRequestDto(findUser));
        return jwtToken;
    }

    public TokenResponseDto appleLogin(AuthRequestDto authRequestDto) {
        String token = authRequestDto.getToken();
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

        String gender = userInfoObject.get("gender").getAsString();
        String email = userInfoObject.get("email").getAsString();

        User findUser = userRepository.findByEmailAndProvider(email, authRequestDto.getProviderType())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER,
                        Map.of("email", email,
                                "gender", gender,
                                "provider", "APPLE")
                ));

        TokenResponseDto jwtToken = jwtService.createToken(new TokenRequestDto(findUser));
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


}
