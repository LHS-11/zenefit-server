package com.cmc.zenefitserver.domain.policy.dto;

import com.cmc.zenefitserver.domain.policy.domain.enums.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(description = "정책 리스트 조회 API respo®nse")
public class PolicyListResponseDto {

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
    private int benefit;

    @ApiModelProperty(notes = "수혜(신청) 정책 여부")
    private boolean applyFlag;

    @ApiModelProperty(notes = "관심 정책 여부")
    private boolean interestFlag;

    @ApiModelProperty(notes = "정책 신청 방법", example = "LETTER, ONLINE, VISIT 등")
    private PolicyMethodType policyMethodType;

    @ApiModelProperty(notes = "정책 신청 방법 설명", example = "우편신청, 방문신청, 온라인신청 등")
    private String policyMethodTypeDescription;

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

    @Builder
    public PolicyListResponseDto(Long policyId, String policyName, String policyLogo, String policyIntroduction, String areaCode, String cityCode, PolicyDateType policyDateType, String policyDateTypeDescription, String policyApplyDenialReason, boolean applyStatus, int benefit, boolean applyFlag, boolean interestFlag, PolicyMethodType policyMethodType, String policyMethodTypeDescription) {
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
        this.applyFlag = applyFlag;
        this.interestFlag = interestFlag;
        this.policyMethodType = policyMethodType;
        this.policyMethodTypeDescription = policyMethodTypeDescription;
    }

    public PolicyListResponseDto(Long policyId, String policyName, String policyLogo, String policyIntroduction, String areaCode, String cityCode, PolicyDateType policyDateType, int benefit, boolean applyFlag, boolean interestFlag, String policyMethodTypeDescription) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.policyLogo = policyLogo;
        this.policyIntroduction = policyIntroduction;
        this.areaCode = areaCode;
        this.cityCode = cityCode;
        this.policyDateType = policyDateType;
        this.benefit = benefit;
        this.applyFlag = applyFlag;
        this.interestFlag = interestFlag;
        this.policyMethodTypeDescription = policyMethodTypeDescription;
    }

    public void updatePolicyMethodType(String applicationProcedureContent) {
        PolicyMethodType findPolicyMethodType = PolicyMethodType.findPolicyMethodTypeByKeywords(applicationProcedureContent);
        this.policyMethodType = findPolicyMethodType;
        this.policyMethodTypeDescription = findPolicyMethodType.getDescription();
    }

//    public PolicyListResponseDto(Long policyId, String policyName, String policyLogo, String policyIntroduction, String areaCode, String cityCode, PolicyDateType policyDateType, int benefit, boolean applyFlag, boolean interestFlag) {
//        this.policyId = policyId;
//        this.policyName = policyName;
//        this.policyLogo = policyLogo;
//        this.policyIntroduction = policyIntroduction;
//        this.areaCode = areaCode;
//        this.cityCode = cityCode;
//        this.policyDateType = policyDateType;
//        this.benefit = benefit;
//        this.applyFlag = applyFlag;
//        this.interestFlag = interestFlag;
//    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class PolicyInfo {
        private Long policyId;

        private String policyName;

        private String policyLogo;

        private String policyIntroduction;

        private String areaCode;

        private String cityCode;

        private PolicyDateType policyDateType;

        private int benefit;

        private boolean applyFlag;

        private boolean interestFlag;

        private LocalDate applySttDate;

        private LocalDate applyEndDate;

        private Set<PolicySplzType> policySplzTypes;

        private int minAge;

        private int maxAge;

//        private Set<JobType> jobTypes;

//        private Set<EducationType> educationTypes;

    }

//    public static PolicyListResponseDto of(Policy policy) {
//        return PolicyListResponseDto.builder()
//                .policyId(policy.getId())
//                .policyName(policy.getPolicyName())
//                .policyApplyDenialReason(policy.getPolicyApplyDenialReason())
//                .areaCode(policy.getAreaCode().getName())
//                .cityCode(policy.getCityCode().getName())
//                .policyLogo(policy.getPolicyLogo())
//                .policyIntroduction(policy.getPolicyIntroduction())
//                .benefit(policy.getBenefit())
//                .applyFlag(true)
//                .interestFlag(true)
//                .build();
//    }
}
