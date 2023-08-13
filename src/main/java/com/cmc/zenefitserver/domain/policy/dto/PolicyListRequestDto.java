package com.cmc.zenefitserver.domain.policy.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

@ToString
@Getter
@NoArgsConstructor
@Validated
@ApiModel(description = "정책 리스트 조회 API request")
public class PolicyListRequestDto {

    @ApiModelProperty(notes = "페이징처리 마지막 정책 ID",example = "20")
    private Long lastPolicyId;

    @ApiModelProperty(notes ="지원 정책 유형",example = "MONEY,LOANS,SOCIAL_SERVICE")
    private String supportPolicyType; // 지원 정책 유형

    @ApiModelProperty(notes ="정책 유형",example = "JOB,RESIDENCE,EDUCATION,WELFARE_CULTURE,PARTICIPATION_RIGHT")
    private String policyType; // 정책 유형
}
