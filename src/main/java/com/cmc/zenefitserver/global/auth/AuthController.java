package com.cmc.zenefitserver.global.auth;

import com.cmc.zenefitserver.global.common.CommonResponse;
import com.cmc.zenefitserver.global.common.request.AuthRequestDto;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Tag(name = "1. oauth",description = "oauth login API")
@RequestMapping("/auth/login")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @Tag(name = "login", description = "Perform login with authentication")
    @Operation(summary = "로그인 API", description = "소셜로그인으로 인증과 인가 처리 후 JWT 토큰 반환 ")
    @PostMapping
    public CommonResponse<TokenResponseDto> login(@Valid @RequestBody AuthRequestDto authRequestDto){
        log.info("==========================login start=======================");
        ProviderType providerType = authRequestDto.getProviderType();
        TokenResponseDto result=null;
        if(providerType==ProviderType.KAKAO){
            result = authService.kakaoLogin(authRequestDto);
        }
        log.info("============================================================");
        return CommonResponse.success(result);
    }

    @GetMapping
    public String test(@RequestParam("code") String code){
        return code;
    }



}
