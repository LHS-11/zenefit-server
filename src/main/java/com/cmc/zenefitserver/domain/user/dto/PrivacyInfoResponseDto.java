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

    @ApiModelProperty(notes = "이용약관 날짜",example = "true")
    private String termsOfServiceUrl;

    @ApiModelProperty(notes = "개인정보처리방침 날짜",example = "true")
    private String privacyUrl;

    @ApiModelProperty(notes = "마켓팅 동의 날짜",example = "true")
    private String marketingUrl;

    @Builder
    public PrivacyInfoResponseDto(LocalDate termsOfServiceDate, LocalDate privacyDate, LocalDate marketingDate, String termsOfServiceUrl, String privacyUrl, String marketingUrl) {
        this.termsOfServiceDate = termsOfServiceDate;
        this.privacyDate = privacyDate;
        this.marketingDate = marketingDate;
        this.termsOfServiceUrl = termsOfServiceUrl;
        this.privacyUrl = privacyUrl;
        this.marketingUrl = marketingUrl;
    }
}
