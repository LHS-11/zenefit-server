package com.cmc.zenefitserver.domain.policy.dto.response;

import com.cmc.zenefitserver.domain.policy.domain.enums.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(description = "정책 리스트 조회 API response")
public class PolicyListInfoDto {

    @ApiModelProperty(notes = "정책 ID", example = "1")
    private Long policyId;

    @ApiModelProperty(notes = "정책 이름")
    private String policyName;

    @ApiModelProperty(notes = "정책 로고")
    private String policyLogo;

    @ApiModelProperty(notes = "정책 소개")
    private String policyIntroduction;

    @ApiModelProperty(notes = "정책 시/도 ( 기관명이 들어갈 곳에 cityCode 가 있다면 같이 텍스트로 넣어야함 )")
    private String areaCode;

    @ApiModelProperty(notes = "정책 시/구 ( 기관명이 들어갈 곳에 areaCode 와 같이 텍스트로 넣어야 함)")
    private String cityCode;

    @ApiModelProperty(notes = "정책 날짜")
    private PolicyDateType policyDateType;

    @ApiModelProperty(notes = "정책 날짜 설명")
    private String policyDateTypeDescription;

    @ApiModelProperty(notes = "정책 불가 사유", example = "신청 기간이 아니에요, ~조건이 맞지 않습니다.")
    private String policyApplyDenialReason;

    @ApiModelProperty(notes = "신청 가능 상태", example = "true or false")
    private boolean applyStatus;

    @ApiModelProperty(notes = "수혜 금액")
    private BigDecimal benefit;

    @ApiModelProperty(notes = "수혜 금액 기간", example = "년 , 월")
    private String benefitPeriod;

    @ApiModelProperty(notes = "수혜(신청) 정책 여부")
    private boolean applyFlag;

    @ApiModelProperty(notes = "관심 정책 여부")
    private boolean interestFlag;

    @ApiModelProperty(notes = "정책 신청 방법", example = "LETTER, ONLINE, VISIT 등")
    private PolicyMethodType policyMethodType;

    @ApiModelProperty(notes = "정책 신청 방법 설명", example = "우편신청, 방문신청, 온라인신청 등")
    private String policyMethodTypeDescription;

    @ApiModelProperty(notes = "정책 신청 URL", example = "www.naver.com")
    private String policyUrl;

    public void updatePolicyLogo(String policyLogo) {
        this.policyLogo = policyLogo;
    }

    public void updateAreaCode(String areaCode) {
        this.areaCode = AreaCode.findName(areaCode);
    }

    public void updateCityCode(String cityCode) {
        this.cityCode = CityCode.findName(cityCode);
    }

    public void updatePolicyDateTypeDescription(PolicyDateType policyDateType) {
        if (policyDateType != null) {
            this.policyDateTypeDescription = policyDateType.getDescription();
        }
    }

    public void updatePolicyApplyDenialReason(String policyApplyDenialReason) {
        this.policyApplyDenialReason = policyApplyDenialReason;
        if (policyApplyDenialReason != null) {
            this.applyStatus = false;
            return;
        }
        this.applyStatus = true;
    }

    public void updatePolicyUrl(String policyUrl) {
        this.policyUrl = policyUrl;
    }

    @Builder
    public PolicyListInfoDto(Long policyId, String policyName, String policyLogo, String policyIntroduction, String areaCode, String cityCode, PolicyDateType policyDateType, String policyDateTypeDescription, String policyApplyDenialReason, boolean applyStatus, BigDecimal benefit, String benefitPeriod, boolean applyFlag, boolean interestFlag, PolicyMethodType policyMethodType, String policyMethodTypeDescription, String policyUrl) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.policyLogo = policyLogo;
        this.policyIntroduction = policyIntroduction;
        this.areaCode = areaCode;
        this.cityCode = cityCode;
        this.policyDateType = policyDateType;
        this.policyDateTypeDescription = policyDateTypeDescription;
        this.policyApplyDenialReason = policyApplyDenialReason;
        this.applyStatus = applyStatus;
        this.benefit = benefit;
        this.benefitPeriod = benefitPeriod;
        this.applyFlag = applyFlag;
        this.interestFlag = interestFlag;
        this.policyMethodType = policyMethodType;
        this.policyMethodTypeDescription = policyMethodTypeDescription;
        this.policyUrl = policyUrl;
    }

    public PolicyListInfoDto(Long policyId, String policyName, String policyLogo, String policyIntroduction, String areaCode, String cityCode, PolicyDateType policyDateType, BigDecimal benefit, String benefitPeriod, boolean applyFlag, boolean interestFlag, String policyMethodTypeDescription, String policyUrl) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.policyLogo = policyLogo;
        this.policyIntroduction = policyIntroduction;
        this.areaCode = areaCode;
        this.cityCode = cityCode;
        this.policyDateType = policyDateType;
        this.benefit = benefit;
        this.benefitPeriod = benefitPeriod;
        this.applyFlag = applyFlag;
        this.interestFlag = interestFlag;
        this.policyMethodTypeDescription = policyMethodTypeDescription;
        this.policyUrl = policyUrl;
    }

    public void updatePolicyMethodType(String applicationProcedureContent) {
        PolicyMethodType findPolicyMethodType = PolicyMethodType.findPolicyMethodTypeByKeywords(applicationProcedureContent);
        this.policyMethodType = findPolicyMethodType;
        this.policyMethodTypeDescription = findPolicyMethodType.getDescription();
    }

    public void updateBenefitPeriod(String benefitPeriod) {
        this.benefitPeriod = benefitPeriod;
    }

}
