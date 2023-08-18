package com.cmc.zenefitserver.domain.policy.dto;

import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@ApiModel(description = "정책 리스트 조회 API response")
public class PolicyListResponseDto {

    @ApiModelProperty(notes = "정책 ID", example = "1")
    private Long policyId;

    @ApiModelProperty(notes = "정책 이름")
    private String policyName;

    @ApiModelProperty(notes = "신청 불가 사유")
    private String policyApplyDenialReason;

    @ApiModelProperty(notes = "정책 시/도 ( 기관명이 들어갈 곳에 cityCode 가 있다면 같이 텍스트로 넣어야함 )")
    private String areaCode;

    @ApiModelProperty(notes = "정책 시/구 ( 기관명이 들어갈 곳에 areaCode 와 같이 텍스트로 넣어야 함)")
    private String cityCode;

    @ApiModelProperty(notes = "정책 로고")
    private String policyLogo;

    @ApiModelProperty(notes = "정책 소개")
    private String policyIntroduction;

    @ApiModelProperty(notes = "신청 가능 상태")
    private String applyStatus;

    @ApiModelProperty(notes = "수혜 금액")
    private int benefit;

    @ApiModelProperty(notes = "신청 정책 여부")
    private boolean applyFlag;

    @ApiModelProperty(notes = "관심 정책 여부")
    private boolean interestFlag;

    public void updateAreaCode(String areaCode) {
        this.areaCode = AreaCode.findName(areaCode);
    }

    public void updateCityCode(String cityCode) {
        this.cityCode = CityCode.findName(cityCode);
    }

    @Builder
    public PolicyListResponseDto(Long policyId, String policyName, String policyApplyDenialReason, String areaCode, String cityCode, String policyLogo, String policyIntroduction, String applyStatus, int benefit, boolean applyFlag, boolean interestFlag) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.policyApplyDenialReason = policyApplyDenialReason;
        this.areaCode = areaCode;
        this.cityCode = cityCode;
        this.policyLogo = policyLogo;
        this.policyIntroduction = policyIntroduction;
        this.applyStatus = applyStatus;
        this.benefit = benefit;
        this.applyFlag = applyFlag;
        this.interestFlag = interestFlag;
    }
}
