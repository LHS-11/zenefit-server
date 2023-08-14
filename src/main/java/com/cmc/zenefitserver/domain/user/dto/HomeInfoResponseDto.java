package com.cmc.zenefitserver.domain.user.dto;


import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

//@ToString
@Getter
@NoArgsConstructor
@ApiModel(description = "홈 정보 조회 API response")
public class HomeInfoResponseDto {

    @ApiModelProperty(notes = "유저 닉네임",example = "paul")
    private String nickname;

    @ApiModelProperty(notes = "유저 캐릭터 이미지 url")
    private String userImage;

    @ApiModelProperty(notes = "유저 수혜 금액")
    private int benefit;

    @ApiModelProperty(notes = "유저의 관심 정책 수",example = "4")
    private int interestPolicyCnt;

    @ApiModelProperty(notes = "유저의 수혜(신청) 정책 수",example = "2")
    private int applyPolicyCnt;

    @ApiModelProperty(notes = "추천 정책")
    private List<HomePolicyInfo> recommendPolicy;

    @ApiModelProperty(notes = "신청 마감일 기준으로 한 정책")
    private List<HomePolicyInfo> endDatePolicy;

    @Builder
    public HomeInfoResponseDto(String nickname, String userImage, int benefit, int interestPolicyCnt, int applyPolicyCnt, List<HomePolicyInfo> recommendPolicy, List<HomePolicyInfo> endDatePolicy) {
        this.nickname = nickname;
        this.userImage = userImage;
        this.benefit = benefit;
        this.interestPolicyCnt = interestPolicyCnt;
        this.applyPolicyCnt = applyPolicyCnt;
        this.recommendPolicy = recommendPolicy;
        this.endDatePolicy = endDatePolicy;
    }

    @Getter
    @NoArgsConstructor
    public static class HomePolicyInfo{

        @ApiModelProperty(notes = "정책 이름")
        private String policyName;

        @ApiModelProperty(notes = "지원 정책 유형")
        private SupportPolicyType supportPolicyType;

        @ApiModelProperty(notes = "지원 정책 유형")
        private String supportPolicyTypeName;

        @ApiModelProperty(notes = "지원 마감일")
        private LocalDate endDate;

        @Builder
        public HomePolicyInfo(String policyName, SupportPolicyType supportPolicyType, String supportPolicyTypeName, LocalDate endDate) {
            this.policyName = policyName;
            this.supportPolicyType = supportPolicyType;
            this.supportPolicyTypeName = supportPolicyTypeName;
            this.endDate = endDate;
        }

    }
}
