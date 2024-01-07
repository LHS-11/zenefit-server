package com.cmc.zenefitserver.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@NoArgsConstructor
@ApiModel(description = "약관 및 개인정보 처리 동의 조회 API response")
public class PrivacyInfoResponseDto {

    @ApiModelProperty(notes = "이용약관 날짜",example = "true")
    private LocalDate termsOfServiceDate;

    @ApiModelProperty(notes = "개인정보처리방침 날짜",example = "true")
    private LocalDate privacyDate;

    @ApiModelProperty(notes = "마켓팅 동의 날짜",example = "true")
    private LocalDate marketingDate;

    @Builder
    public PrivacyInfoResponseDto(LocalDate termsOfServiceDate, LocalDate privacyDate, LocalDate marketingDate) {
        this.termsOfServiceDate = termsOfServiceDate;
        this.privacyDate = privacyDate;
        this.marketingDate = marketingDate;
    }
}
