package com.cmc.zenefitserver.domain.policy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyContentFeignDto {

    //    private String policyBizId;
    private String result;

}
