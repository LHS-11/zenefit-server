package com.cmc.zenefitserver.global.auth;

import com.cmc.zenefitserver.global.common.CommonResponse;
import com.cmc.zenefitserver.global.common.request.AuthRequestDto;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import com.cmc.zenefitserver.global.error.exception.ControllerException;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Api(tags = "1. oauth",description = "oauth login API")
@RequestMapping("/auth/login")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인 API", description = "소셜로그인으로 인증과 인가 처리 후 JWT 토큰 반환 ")
    @PostMapping
    public CommonResponse<TokenResponseDto> login(@Valid @RequestBody AuthRequestDto authRequestDto, BindingResult bindingResult){
        log.info("==========================login start=======================");
        if(bindingResult.hasErrors()){
            log.error("error 발생 ");
            System.out.println("bindingResult.getFieldError().getCode() = " + bindingResult.getFieldError().getDefaultMessage());
            throw new ControllerException(bindingResult);
        }

        ProviderType providerType = authRequestDto.getProviderType();

        TokenResponseDto result=null;
        if(providerType==ProviderType.KAKAO){
            result = authService.kakaoLogin(authRequestDto);
        }
        if(providerType==ProviderType.APPLE){
            result = authService.appleLogin(authRequestDto);
        }
        log.info("==========================login end=======================");
        return CommonResponse.success(result);
    }

//    @GetMapping
//    public String test(@RequestParam("code") String code){
//        return code;
//    }



}
