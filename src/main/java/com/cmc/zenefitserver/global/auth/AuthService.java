package com.cmc.zenefitserver.global.auth;

import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.auth.jwt.JwtService;
import com.cmc.zenefitserver.global.auth.kakao.KakaoAccount;
import com.cmc.zenefitserver.global.auth.kakao.KakaoLoginService;
import com.cmc.zenefitserver.global.common.request.AuthRequestDto;
import com.cmc.zenefitserver.global.common.request.TokenRequestDto;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final KakaoLoginService kakaoLoginService;
    private final JwtService jwtService;

    public TokenResponseDto kakaoLogin(AuthRequestDto authRequestDto){

        String code = authRequestDto.getToken();
        KakaoAccount kakaoAccount = kakaoLoginService.getInfo(code).getKakaoAccount();

        // 이메일로 회원 조회시 없으면 이메일과 함께 오류 반환
        User findUser = userRepository.findByEmail(kakaoAccount.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER,
                        Map.of("email", kakaoAccount.getEmail(),
                                "gender",kakaoAccount.getGender())
                ));

        // 이메일로 회원 조회시 있으면 로그인하고 자체 JWT 만들어서 Access Token 과 Refresh Token 반환
        TokenResponseDto jwtToken = jwtService.createToken(new TokenRequestDto(findUser));
        return jwtToken;
    }
}
