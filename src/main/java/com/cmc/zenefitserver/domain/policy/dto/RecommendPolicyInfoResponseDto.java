package com.cmc.zenefitserver.domain.policy.dto;

import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyDateType;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor
@ApiModel(description = "추천 정책 정보 조회 API response")
public class RecommendPolicyInfoResponseDto {

    @ApiModelProperty(notes = "추천 정책 정보")
    private List<recommendPolicyInfo> policyInfos;

    @Builder
    public RecommendPolicyInfoResponseDto(List<recommendPolicyInfo> policyInfos) {
        this.policyInfos = policyInfos;
    }

    @Getter
    @NoArgsConstructor
    public static class recommendPolicyInfo {

        @ApiModelProperty(notes = "정책 지원 유형", example = "MONEY, LOANS, SOCIAL_SERVICE")
        private SupportPolicyType supportType;

        @ApiModelProperty(notes = "정책 지원 유형 이름", example = "현금, 대출, 사회서비스")
        private String supportTypeDescription;

        @ApiModelProperty(notes = "정책 ID", example = "24")
        private Long policyId;

        @ApiModelProperty(notes = "정책 이름", example = "청년도약계좌")
        private String policyName;

        @ApiModelProperty(notes = "정책 지역 로고", example = "24")
        private String policyLogo;

        @ApiModelProperty(notes = "정책 시/도 이름", example = "서울")
        private String policyAreaCode;

        @ApiModelProperty(notes = "정책 시/구 이름", example = "강서구")
        private String policyCityCode;

        @ApiModelProperty(notes = "정책 소개", example = "월 70만원을 5년 납입하면 약 5,000만원을 적립할 수 있는 청년도약계좌")
        private String policyIntroduction;

        @ApiModelProperty(notes = "지원 정책 별 정책 수", example = "24")
        private int supportTypePolicyCnt;

        @ApiModelProperty(notes = "정책 수혜금액", example = "1560000")
        private int benefit;

        @ApiModelProperty(notes = "정책 기간 타입", example = "1560000")
        private String policyDateType;

        @Builder
        public recommendPolicyInfo(SupportPolicyType supportType, String supportTypeDescription, Long policyId, String policyName, String policyLogo, String policyAreaCode, String policyCityCode, String policyIntroduction, int supportTypePolicyCnt, int benefit, String policyDateType) {
            this.supportType = supportType;
            this.supportTypeDescription = supportTypeDescription;
            this.policyId = policyId;
            this.policyName = policyName;
            this.policyLogo = policyLogo;
            this.policyAreaCode = policyAreaCode;
            this.policyCityCode = policyCityCode;
            this.policyIntroduction = policyIntroduction;
            this.supportTypePolicyCnt = supportTypePolicyCnt;
            this.benefit = benefit;
            this.policyDateType = policyDateType;
        }

        public void upgradeCityCode(String name) {
            this.policyCityCode = name;
        }
    }
}
