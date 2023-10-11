package com.cmc.zenefitserver.domain.policy.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@ApiModel(description = "정책 상세 정보 조회 API response")
public class PolicyCountResponseDto {

    @ApiModelProperty(notes = "유저에 맞는 정책의 총 수",example = "56")
    private int policyCnt;

    @Builder
    public PolicyCountResponseDto(int policyCnt) {
        this.policyCnt = policyCnt;
    }
}
