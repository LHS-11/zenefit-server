package com.cmc.zenefitserver.global.infra.notification.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@ApiModel(description = "알림 내역 정보 API response")
public class NotificationListInfoResponseDto {

    @ApiModelProperty(notes = "알림 id",example ="알림 ID" )
    private Long notificationId;

    @ApiModelProperty(notes = "알림 제목",example = "[청년도약계좌] 신청 시작 D-3")
    private String title;

    @ApiModelProperty(notes = "알림 내용",example = "이제 곧 신청이 시작돼요!")
    private String content;

    @ApiModelProperty(notes = "알림 로고 URL")
    private String logo;

    @Builder
    public NotificationListInfoResponseDto(String title, String content, String logo) {
        this.title = title;
        this.content = content;
        this.logo = logo;
    }
}
