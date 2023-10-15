package com.cmc.zenefitserver.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "로그인 API Response")
public class TokenResponseDto {

    @ApiModelProperty(notes = "액세스토큰", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ~~~~")
    private final String accessToken;

    @ApiModelProperty(notes = "리프레시 토큰", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ~~~~")
    private final String refreshToken;

    @ApiModelProperty(notes = "유저 닉네임",example = "임화섭")
    private String nickname;

    public void updateNickname(String nickname){
        this.nickname=nickname;
    }

    @Builder
    public TokenResponseDto(String accessToken, String refreshToken, String nickname) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.nickname = nickname;
    }
}
