package com.cmc.zenefitserver.domain.policy.dto;

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

    @ApiModelProperty(notes = "유저에 맞는 정책의 총 수",example = "56")
    private int policyCnt;

    @ApiModelProperty(notes = "추천 정책 정보")
    private List<recommendPolicyInfo> policyInfos;

    @Builder
    public RecommendPolicyInfoResponseDto(int policyCnt, List<recommendPolicyInfo> policyInfos) {
        this.policyCnt = policyCnt;
        this.policyInfos = policyInfos;
    }

    @Getter
    @NoArgsConstructor
    public static class recommendPolicyInfo {

        @ApiModelProperty(notes = "정책 지원 유형",example = "현금, 대출, 사회서비스")
        private String supportType;

        @ApiModelProperty(notes = "정책 ID",example = "24")
        private Long policyId;

        @ApiModelProperty(notes = "정책 이름",example = "청년도약계좌")
        private String policyName;

        @ApiModelProperty(notes = "정책 지역 로고",example = "24")
        private String policyLogo;

        @ApiModelProperty(notes = "정책 시/도 이름",example = "서울")
        private String policyAreaCode;

        @ApiModelProperty(notes = "정책 시/구 이름",example = "강서구")
        private String policyCityCode;

        @ApiModelProperty(notes = "정책 소개",example = "월 70만원을 5년 납입하면 약 5,000만원을 적립할 수 있는 청년도약계좌")
        private String policyIntroduction;

        @ApiModelProperty(notes = "정책 신청 상태",example = "상시, 기간")
        private String applyStatus;

        @ApiModelProperty(notes = "지원 정책 별 정책 수",example = "24")
        private int supportTypePolicyCnt;

        @ApiModelProperty(notes = "정책 수혜금액",example = "1560000")
        private int benefit;

        @Builder
        public recommendPolicyInfo(String supportType, Long policyId, String policyName, String policyLogo, String policyAreaCode, String policyCityCode, String policyIntroduction, String applyStatus, int supportTypePolicyCnt, int benefit) {
            this.supportType = supportType;
            this.policyId = policyId;
            this.policyName = policyName;
            this.policyLogo = policyLogo;
            this.policyAreaCode = policyAreaCode;
            this.policyCityCode = policyCityCode;
            this.policyIntroduction = policyIntroduction;
            this.applyStatus = applyStatus;
            this.supportTypePolicyCnt = supportTypePolicyCnt;
            this.benefit = benefit;
        }

        public void upgradeCityCode(String name) {
            this.policyCityCode = name;
        }
    }
}
