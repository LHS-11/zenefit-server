package com.cmc.zenefitserver.domain.policy.dto.request;


import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ToString
@Getter
@NoArgsConstructor
@ApiModel(description = "정책 리스트 검색 API request")
public class SearchPolicyListRequestDto {

    @ApiModelProperty(notes = "페이징처리 마지막 정책 ID", example = "20")
    private Long lastPolicyId;

    @ApiModelProperty(notes = "지원 정책 유형", example = "MONEY,LOANS,SOCIAL_SERVICE")
    private SupportPolicyType supportPolicyType; // 지원 정책 유형

    @ApiModelProperty(notes = "정책 유형", example = "JOB,RESIDENCE,EDUCATION,WELFARE_CULTURE,PARTICIPATION_RIGHT")
    private PolicyCode policyType; // 정책 유형

    @ApiModelProperty(notes = "검색할 정책 이름 키워드", example = "청년도약")
    @NotNull(message = "검색할 키워드를 넣어주세요.")
    private String keyword;
}
