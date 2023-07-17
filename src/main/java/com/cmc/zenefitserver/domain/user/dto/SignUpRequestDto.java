package com.cmc.zenefitserver.domain.user.dto;

import com.cmc.zenefitserver.domain.job.domain.Job;
import com.cmc.zenefitserver.domain.user.domain.Address;
import com.cmc.zenefitserver.domain.user.domain.EducationType;
import com.cmc.zenefitserver.domain.user.domain.Gender;
import com.cmc.zenefitserver.global.auth.ProviderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.Set;


@Getter
@NoArgsConstructor
@Validated
@Schema(description = "회원가입 API request")
public class SignUpRequestDto {

    @Schema(description = "이메일",example = "awdsawd@naver.com")
    private String email;

    @Schema(description = "소셜 타입",example = "KAKAO")
    private ProviderType provider;

    @Schema(description = "닉네임",example = "paul")
    private String nickname;

    @Schema(description = "나이",example = "25")
    private int age;

    @Schema(description = "주소",example = "서울시/강서구")
    private Address address;

    @Schema(description = "작년 소득",example = "50000000")
    private int lastYearIncome;

    @Schema(description = "학력",example = "대학 재학")
    private EducationType educationType;

    @Schema(description = "직업",example = "재직자")
    private Set<Job> jobs;

    @Schema(description = "성별",example = "MALE")
    private Gender gender;

    @Builder
    public SignUpRequestDto(String email, ProviderType provider, String nickname, int age, Address address, int lastYearIncome, EducationType educationType, Set<Job> jobs, Gender gender) {
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
