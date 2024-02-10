package com.cmc.zenefitserver.domain.policy.dto.response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(description = "지원할 수 있는 정책의 수와 유저 닉네임 조회 API response")
public class PolicyCountResponseDto {

    @ApiModelProperty(notes = "유저 닉네임")
    private String nickname;

    @ApiModelProperty(notes = "유저에 맞는 정책의 총 수",example = "56")
    private int policyCnt;

    @Builder
    public PolicyCountResponseDto(String nickname, int policyCnt) {
        this.nickname = nickname;
        this.policyCnt = policyCnt;
    }
}
