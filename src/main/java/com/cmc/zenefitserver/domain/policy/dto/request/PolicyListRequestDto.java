package com.cmc.zenefitserver.domain.policy.dto.request;


import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
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

    @ApiModelProperty(notes = "지원 정책 유형", example = "MONEY,LOANS,SOCIAL_SERVICE")
    private SupportPolicyType supportPolicyType; // 지원 정책 유형

    @ApiModelProperty(notes = "정책 유형", example = "JOB,RESIDENCE,EDUCATION,WELFARE_CULTURE,PARTICIPATION_RIGHT")
    private PolicyCode policyType; // 정책 유형
}
