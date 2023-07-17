package com.cmc.zenefitserver.domain.user.api;

import antlr.Token;
import com.cmc.zenefitserver.domain.user.application.UserService;
import com.cmc.zenefitserver.domain.user.domain.Address;
import com.cmc.zenefitserver.domain.user.dto.SignUpRequestDto;
import com.cmc.zenefitserver.global.common.CommonResponse;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Tag(name = "2. user",description = "user API")
@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public CommonResponse<TokenResponseDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto){
        log.info("==========================signup start=======================");
        TokenResponseDto tokenResponseDto = userService.signUp(signUpRequestDto);
        return CommonResponse.success(tokenResponseDto);
    }

}
