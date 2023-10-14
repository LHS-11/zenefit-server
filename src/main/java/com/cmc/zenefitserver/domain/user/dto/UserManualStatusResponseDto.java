package com.cmc.zenefitserver.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@ApiModel(description = "유저 메뉴얼 조회 API response")
public class UserManualStatusResponseDto {

    @ApiModelProperty(notes = "메뉴얼 상태",example = "true")
    private boolean manualStatus;

    @Builder
    public UserManualStatusResponseDto(boolean manualStatus) {
        this.manualStatus = manualStatus;
    }
}
