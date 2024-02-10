package com.cmc.zenefitserver.domain.policy.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.data.domain.Page;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(description = "정책 리스트 조회 API respo®nse")
public class PolicyListResponseDto {

    private Page<PolicyListInfoDto> policyListInfoResponseDto;

    private int pageNumber;

    private boolean last;

    @Builder
    public PolicyListResponseDto(Page<PolicyListInfoDto> policyListInfoResponseDto, int pageNumber, boolean last) {
        this.policyListInfoResponseDto = policyListInfoResponseDto;
        this.pageNumber = pageNumber;
        this.last = last;
    }

}
