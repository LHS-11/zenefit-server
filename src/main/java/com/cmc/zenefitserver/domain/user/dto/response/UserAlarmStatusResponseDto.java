package com.cmc.zenefitserver.domain.user.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(description = "유저 메뉴얼 조회 API response")
public class UserAlarmStatusResponseDto {

    @ApiModelProperty(notes = "메뉴얼 상태",example = "true")
    private boolean alarmStatus;

    @Builder
    public UserAlarmStatusResponseDto(boolean alarmStatus) {
        this.alarmStatus = alarmStatus;
    }
}
