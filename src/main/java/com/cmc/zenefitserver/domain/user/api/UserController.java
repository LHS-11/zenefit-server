package com.cmc.zenefitserver.domain.user.api;

import com.cmc.zenefitserver.domain.user.application.UserService;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.user.dto.ModifyRequestDto;
import com.cmc.zenefitserver.domain.user.dto.SignUpRequestDto;
import com.cmc.zenefitserver.global.annotation.AuthUser;
import com.cmc.zenefitserver.global.common.CommonResponse;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@Api(tags = "2. user",description = "user API")
@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API",description = "회원가입을 진행합니다. \n 로그인 실패시 이메일과 필요한 정보들을 반환하여 해당 값을 이용해서 회원가입")
    public CommonResponse<TokenResponseDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto){
        log.info("==========================signup start=======================");
        TokenResponseDto tokenResponseDto = userService.signUp(signUpRequestDto);
        return CommonResponse.success(tokenResponseDto);
    }

    @PostMapping("/modify")
    @Operation(summary = "회원정보수정 API",description = "회원정보를 수정합니다.")
    public CommonResponse<User> modify(@Valid @RequestBody ModifyRequestDto modifyRequestDto,
                                   @ApiIgnore @AuthUser User user){
        User result = userService.modify(modifyRequestDto, user);
        return CommonResponse.success(result);
    }

}
