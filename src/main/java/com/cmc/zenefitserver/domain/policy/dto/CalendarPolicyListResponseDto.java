package com.cmc.zenefitserver.domain.policy.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@NoArgsConstructor
@ApiModel(description = "특정 날짜에 따른 정책 리스트 검색 API response")
public class CalendarPolicyListResponseDto {

    @ApiModelProperty(notes = "정책 ID",example = "12")
    private Long policyId;

    @ApiModelProperty(notes = "정책 이름",example = "청년도약계좌")
    private String policyName;

    @ApiModelProperty(notes = "정책 신청 가능 상태",example = "상시신청,기간신청")
    private String policyApplyStatus;

    @ApiModelProperty(notes = "정책 기관 로고 url")
    private String policyAgencyLogo;

    @ApiModelProperty(notes = "정책 신청 시작일 또는 종료일",example = "2023-06-15")
    private LocalDate policySttDateOrEndDate;

    @Builder
    public CalendarPolicyListResponseDto(Long policyId, String policyName, String policyApplyStatus, String policyAgencyLogo, LocalDate policySttDateOrEndDate) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.policyApplyStatus = policyApplyStatus;
        this.policyAgencyLogo = policyAgencyLogo;
        this.policySttDateOrEndDate = policySttDateOrEndDate;
    }

}
