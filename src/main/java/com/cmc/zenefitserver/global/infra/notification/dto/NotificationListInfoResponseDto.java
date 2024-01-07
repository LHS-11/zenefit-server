package com.cmc.zenefitserver.global.infra.notification.dto;

import com.cmc.zenefitserver.domain.policy.domain.enums.SearchDateType;
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

    @ApiModelProperty(notes = "알림 타입", example = "STT_DATE, END_DATE")
    private SearchDateType searchDateType;

    @ApiModelProperty(notes = "정책 ID", example = "1")
    private Long policyId;

    @Builder
    public NotificationListInfoResponseDto(Long notificationId, String title, String content, String logo, SearchDateType searchDateType, Long policyId) {
        this.notificationId = notificationId;
        this.title = title;
        this.content = content;
        this.logo = logo;
        this.searchDateType = searchDateType;
        this.policyId = policyId;
    }
}
