package com.cmc.zenefitserver.domain.user.dto;

import com.cmc.zenefitserver.domain.user.domain.Address;
import com.cmc.zenefitserver.domain.user.domain.EducationType;
import com.cmc.zenefitserver.domain.user.domain.Gender;
import com.cmc.zenefitserver.domain.user.domain.JobType;
import com.cmc.zenefitserver.global.auth.ProviderType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.Set;


@Getter
@NoArgsConstructor
@Validated
@ApiModel(description = "회원가입 API request")
public class SignUpRequestDto {

    @ApiModelProperty(notes = "이메일",example = "awdsawd@naver.com")
    private String email;

    @ApiModelProperty(notes = "소셜 타입",example = "KAKAO")
    private ProviderType provider;

    @ApiModelProperty(notes ="닉네임",example = "paul")
    private String nickname;

    @ApiModelProperty(notes = "나이",example = "25")
    private int age;

    @ApiModelProperty(notes = "주소",example = "서울시/강서구")
    private Address address;

    @ApiModelProperty(notes = "작년 소득",example = "50000000")
    private int lastYearIncome;

    @ApiModelProperty(notes = "학력",example = "대학 재학")
    private EducationType educationType;

    @ApiModelProperty(notes = "직업",example = "재직자")
    private Set<JobType> jobs;

    @ApiModelProperty(notes = "성별",example = "MALE")
    private Gender gender;

    @Builder
    public SignUpRequestDto(String email, ProviderType provider, String nickname, int age, Address address, int lastYearIncome, EducationType educationType, Set<JobType> jobs, Gender gender) {
        this.email = email;
        this.provider = provider;
        this.nickname = nickname;
        this.age = age;
        this.address = address;
        this.lastYearIncome = lastYearIncome;
        this.educationType = educationType;
        this.jobs = jobs;
        this.gender = gender;
    }
}
