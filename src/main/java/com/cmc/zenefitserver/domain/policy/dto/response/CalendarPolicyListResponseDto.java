package com.cmc.zenefitserver.domain.policy.dto.response;

import com.cmc.zenefitserver.domain.policy.domain.enums.DenialReasonType;
import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyMethodType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(description = "특정 날짜에 따른 정책 리스트 검색 API response")
public class CalendarPolicyListResponseDto {

    @ApiModelProperty(notes = "정책 ID", example = "12")
    private Long policyId;

    @ApiModelProperty(notes = "정책 이름", example = "청년도약계좌")
    private String policyName;

    @ApiModelProperty(notes = "정책 신청 가능 상태", example = "true or false")
    private boolean applyStatus;

    @ApiModelProperty(notes = "정책 신청 방법", example = "방문 신청, 우편 신청, 홈페이지 신청")
    private PolicyMethodType applyProcedure;

    @ApiModelProperty(notes = "정책 기관 로고 url")
    private String policyAgencyLogo;

    @ApiModelProperty(notes = "정책 신청 시작일", example = "2023-06-15")
    private LocalDate applySttDate;

    @ApiModelProperty(notes = "정책 신청 종료일", example = "2023-06-15")
    private LocalDate applyEndDate;

    public void upgradeApplyStatus(DenialReasonType denialReasonType) {
        if (denialReasonType != null) {
            this.applyStatus = false;
            return;
        }
        this.applyStatus = true;
    }

    @Builder
    public CalendarPolicyListResponseDto(Long policyId, String policyName, boolean applyStatus, PolicyMethodType applyProcedure, String policyAgencyLogo, LocalDate applySttDate, LocalDate applyEndDate) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.applyStatus = applyStatus;
        this.applyProcedure = applyProcedure;
        this.policyAgencyLogo = policyAgencyLogo;
        this.applySttDate = applySttDate;
        this.applyEndDate = applyEndDate;
    }
}
