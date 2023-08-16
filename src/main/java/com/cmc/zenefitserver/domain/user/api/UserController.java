package com.cmc.zenefitserver.domain.user.api;

import com.cmc.zenefitserver.domain.user.application.UserService;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.user.dto.*;
import com.cmc.zenefitserver.global.annotation.AuthUser;
import com.cmc.zenefitserver.global.common.CommonResponse;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

import static com.cmc.zenefitserver.global.error.ErrorCode.INVALID_NICKNAME;

@Slf4j
@Api(tags = "2. user", description = "user API")
@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "회원가입을 진행합니다. \n 로그인 실패시 이메일과 필요한 정보들을 반환하여 해당 값을 이용해서 회원가입")
    public CommonResponse<TokenResponseDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto, BindingResult result) {
        log.info("회원가입 API");

        if (result.hasErrors()) {
            return CommonResponse.failure(INVALID_NICKNAME);
        }

        TokenResponseDto tokenResponseDto = userService.signUp(signUpRequestDto);
        return CommonResponse.success(tokenResponseDto);
    }

    @PostMapping("/modify")
    @Operation(summary = "회원정보수정 API", description = "회원정보를 수정합니다.")
    public CommonResponse<User> modify(@Valid @RequestBody ModifyRequestDto modifyRequestDto,
                                       @ApiIgnore @AuthUser User user) {
        log.info("회원정보수정 API, user = {}", user.getUserId());
        User result = userService.modify(modifyRequestDto, user);
        return CommonResponse.success(result);
    }

    @GetMapping("/area")
    @Operation(summary = "시/도 조회 API ", description = "회원가입시 지역 설정에서 시/도를 가져옵니다.")
    public CommonResponse<List<String>> getAreaCode() {
        log.info("시/도 조회 API");
        List<String> result = userService.getAreaCodes();
        return CommonResponse.success(result);
    }

    @GetMapping("/city")
    @Operation(summary = "시/군/구 조회 API ", description = "회원가입시 지역 설정에서 시/군/구를 가져옵니다.")
    public CommonResponse<List<String>> getCityCode(@RequestParam String area) {
        log.info("시/군/구 조회 API");
        List<String> result = userService.getCityCodes(area);
        return CommonResponse.success(result);
    }

    @GetMapping("/social")
    @Operation(summary = "소셜 로그인 정보 (이메일) 조회 API", description = "설정에서 로그인 정보를 조회할 때 사용합니다.")
    public CommonResponse<SocialInfoResponseDto> getSocialInfo(@AuthUser User user) {
        log.info("소셜 로그인 정보 조회 API, user = {}", user.getUserId());
        SocialInfoResponseDto result = userService.getSocialInfo(user);
        return CommonResponse.success(result);
    }

    @GetMapping
    @Operation(summary = "개인 유저 정보 조회 API", description = "설정에서 개인정보 조회시 해당 유저 정보를 가져옵니다.")
    public CommonResponse<UserInfoResponseDto> getUserInfo(@AuthUser User user) {
        log.info("개인 유저 정보 조회 API, user = {}", user.getUserId());
        UserInfoResponseDto result = userService.getUserInfo(user);
        return CommonResponse.success(result);
    }

    @GetMapping("/home")
    @Operation(summary = "홈 정보 조회 API", description = "홈 화면 정보를 가져옵니다.")
    public CommonResponse<HomeInfoResponseDto> getHomeInfo(@AuthUser User user) {
        log.info("홈 정보 조회 API, user = {}", user.getUserId());
        HomeInfoResponseDto result = userService.getHomeInfo(user);
        return CommonResponse.success(result);
    }

    @PatchMapping("/fcm_token")
    @Operation(summary = "fcm 토큰 업데이트 API", description = "사용자의 fcm 토큰을 업데이트합니다.")
    public CommonResponse<String> updateFcmToken(@AuthUser User user, @RequestParam String fcmToken) {
        log.info("FCM 토큰 업데이트 API, user = {}", user.getUserId());
        userService.updateFcmToken(user, fcmToken);
        return CommonResponse.success(null);
    }

}
