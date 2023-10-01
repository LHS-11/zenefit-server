package com.cmc.zenefitserver.domain.user.dto;

import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import com.cmc.zenefitserver.domain.user.domain.EducationType;
import com.cmc.zenefitserver.domain.user.domain.Gender;
import com.cmc.zenefitserver.domain.user.domain.JobType;
import com.cmc.zenefitserver.global.auth.ProviderType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Set;


@Getter
@NoArgsConstructor
@Validated
@ApiModel(description = "회원가입 API request")
public class SignUpRequestDto {

    @NotNull(message = "이메일을 입력해주세요.")
    @ApiModelProperty(notes = "이메일", example = "awdsawd@naver.com")
    private String email;

    @NotNull(message = "소셜 타입을 입력해주세요.")
    @ApiModelProperty(notes = "소셜 타입", example = "KAKAO")
    private ProviderType provider;

//    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{1,7}$", message = "닉네임은 1~7자의 영문 대소문자, 숫자, 한글로만 입력해주세요.")
//    @ApiModelProperty(notes ="닉네임",example = "paul")
//    private String nickname;

    @NotNull(message = "나이를 입력해주세요.")
    @ApiModelProperty(notes = "나이", example = "25")
    private int age;

    @NotNull(message = "areaCode 를 입력해주세요.")
    @ApiModelProperty(notes = "시/도", example = "서울")
    private AreaCode areaCode;

    @NotNull(message = "cityCode 를 입력해주세요.")
    @ApiModelProperty(notes = "시/구", example = "강서구")
    private CityCode cityCode;

    @NotNull(message = "작년 소득을 입력해주세요.")
    @ApiModelProperty(notes = "작년 소득", example = "50000000")
    private int lastYearIncome;

    @ApiModelProperty(notes = "학력", example = "대학 재학")
    private EducationType educationType;

    @ApiModelProperty(notes = "직업", example = "재직자")
    private Set<JobType> jobs;

    @ApiModelProperty(notes = "성별", example = "MALE")
    private Gender gender;

    @Builder
    public SignUpRequestDto(String email, ProviderType provider, int age, AreaCode areaCode, CityCode cityCode, int lastYearIncome, EducationType educationType, Set<JobType> jobs, Gender gender) {
        this.email = email;
        this.provider = provider;
//        this.nickname = nickname;
        this.age = age;
        this.areaCode = areaCode;
        this.cityCode = cityCode;
        this.lastYearIncome = lastYearIncome;
        this.educationType = educationType;
        this.jobs = jobs;
        this.gender = gender;
    }
}
