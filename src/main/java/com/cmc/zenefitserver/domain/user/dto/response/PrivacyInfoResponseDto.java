package com.cmc.zenefitserver.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(description = "약관 및 개인정보 처리 동의 조회 API response")
public class PrivacyInfoResponseDto {

    @ApiModelProperty(notes = "이용약관 날짜",example = "true")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate termsOfServiceDate;

    @ApiModelProperty(notes = "개인정보처리방침 날짜",example = "true")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate privacyDate;

    @ApiModelProperty(notes = "마켓팅 동의 날짜",example = "true")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate marketingDate;

    @ApiModelProperty(notes = "이용약관 url",example = "true")
    private String termsOfServiceUrl;

    @ApiModelProperty(notes = "개인정보처리방침 url",example = "true")
    private String privacyUrl;

    @ApiModelProperty(notes = "마켓팅 동의 url",example = "true")
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
