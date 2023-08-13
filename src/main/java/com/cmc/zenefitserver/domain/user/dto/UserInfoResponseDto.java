package com.cmc.zenefitserver.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@ToString
@Getter
@NoArgsConstructor
@ApiModel(description = "소셜 로그인 정보 (이메일) 조회 API response")
public class UserInfoResponseDto {

    @ApiModelProperty(notes = "유저 닉네임",example = "paul")
    private String nickname;

    @ApiModelProperty(notes = "유저 나이",example = "34")
    private int age;

    @ApiModelProperty(notes = "유저 지역",example = "서울시")
    private String area;

    @ApiModelProperty(notes = "유저 소속 지역",example = "강남구")
    private String city;

    @ApiModelProperty(notes = "유저 작년 소득",example = "600000")
    private int lastYearIncome;

    @ApiModelProperty(notes = "유저 학력",example = "대학 재학")
    private String educationType;

    @ApiModelProperty(notes = "유저 직업",example = "미취업자, 단기근로자")
    private Set<String> jobs;

    @ApiModelProperty(notes = "유저 기타 정보 성별",example = "남성")
    private String gender;

    @ApiModelProperty(notes = "유저 기타 정보 중소기업 여부",example = "true")
    private boolean smallBusiness;

    @ApiModelProperty(notes = "유저 기타 정보 군인 여부",example = "true")
    private boolean soldier;

    @ApiModelProperty(notes = "유저 기타 정보 저소득층 여부",example = "true")
    private boolean lowIncome;

    @ApiModelProperty(notes = "유저 기타 정보 장애인 여부",example = "true")
    private boolean disabled;

    @ApiModelProperty(notes = "유저 기타 정보 지역인재 여부",example = "true")
    private boolean localTalent;

    @ApiModelProperty(notes = "유저 기타 정보 농부 여부",example = "true")
    private boolean farmer;

    @Builder
    public UserInfoResponseDto(String nickname, int age, String area, String city, int lastYearIncome, String educationType, Set<String> jobs, String gender, boolean smallBusiness, boolean soldier, boolean lowIncome, boolean disabled, boolean localTalent, boolean farmer) {
        this.nickname = nickname;
        this.age = age;
        this.area = area;
        this.city = city;
        this.lastYearIncome = lastYearIncome;
        this.educationType = educationType;
        this.jobs = jobs;
        this.gender = gender;
        this.smallBusiness = smallBusiness;
        this.soldier = soldier;
        this.lowIncome = lowIncome;
        this.disabled = disabled;
        this.localTalent = localTalent;
        this.farmer = farmer;
    }
}
