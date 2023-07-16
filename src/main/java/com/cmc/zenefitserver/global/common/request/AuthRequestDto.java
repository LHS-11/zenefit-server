package com.cmc.zenefitserver.global.common.request;

import com.cmc.zenefitserver.global.auth.ProviderType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
@ApiModel(value = "로그인 API Request")
public class AuthRequestDto {

    @ApiModelProperty(notes = "소셜 로그인 유형", example = "APPLE, KAKAO, GOOGLE, NAVER")
    @NotNull
    private ProviderType providerType;

    @ApiModelProperty(notes = "소셜 토큰 (kakao는 code)", example = "p1b3M_ikmtHivvFJqwY5bXAYg-ilCq4E7DnJwlT5CisM0wAAAYOPy1oR")
    @NotNull
    private String token;
}
