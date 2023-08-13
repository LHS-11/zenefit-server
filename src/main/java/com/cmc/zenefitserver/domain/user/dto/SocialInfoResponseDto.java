package com.cmc.zenefitserver.domain.user.dto;

import com.cmc.zenefitserver.global.auth.ProviderType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

@ToString
@Getter
@NoArgsConstructor
@ApiModel(description = "소셜 로그인 정보 (이메일) 조회 API response")
public class SocialInfoResponseDto {

    @ApiModelProperty(notes = "소셜 로그인 이메일",example = "adwada@naver.com")
    private String email;

    @ApiModelProperty(notes = "소셜 로그인 타입",example = "KAKAO, APPLE")
    private ProviderType provider;

    @Builder
    public SocialInfoResponseDto(String email, ProviderType provider) {
        this.email = email;
        this.provider = provider;
    }
}
