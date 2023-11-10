package com.cmc.zenefitserver.domain.userpolicy.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(description = "정책 갯수 조회 API response")
public class PolicySizeResponseDto {

    @ApiModelProperty(notes = "정책 수",example = "12")
    private int size;

    @Builder
    public PolicySizeResponseDto(int size) {
        this.size = size;
    }
}
