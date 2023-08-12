package com.cmc.zenefitserver.domain.policy.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@ApiModel(description = "정책 리스트 조회 API response")
public class PolicyListResponseDto {

    @ApiModelProperty(notes = "정책 ID",example = "1")
    private Long policyId;

    @ApiModelProperty(notes = "정책 이름")
    private String policyName;

    @ApiModelProperty(notes = "신청 불가 사유")
    private String policyApplyDenialReason;

    @ApiModelProperty(notes = "기관")
    private String policyAgency;

    @ApiModelProperty(notes = "기관 로고")
    private String policyAgencyLogo;

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

    @Builder
    public PolicyListResponseDto(Long policyId, String policyName, String policyApplyDenialReason, String policyAgency, String policyAgencyLogo, String policyIntroduction, String applyStatus, int benefit, boolean applyFlag, boolean interestFlag) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.policyApplyDenialReason = policyApplyDenialReason;
        this.policyAgency = policyAgency;
        this.policyAgencyLogo = policyAgencyLogo;
        this.policyIntroduction = policyIntroduction;
        this.applyStatus = applyStatus;
        this.benefit = benefit;
        this.applyFlag = applyFlag;
        this.interestFlag = interestFlag;
    }
}
