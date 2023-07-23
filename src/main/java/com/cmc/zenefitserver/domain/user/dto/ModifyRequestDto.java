package com.cmc.zenefitserver.domain.user.dto;

import com.cmc.zenefitserver.domain.user.domain.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;
import java.util.Set;

@ToString
@Getter
@NoArgsConstructor
@Validated
@ApiModel(description = "회원정보수정 API request")
public class ModifyRequestDto {

    @ApiModelProperty(notes ="닉네임",example = "paul")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{1,7}$", message = "닉네임은 1~7자의 영문 대소문자, 숫자, 한글로만 입력해주세요.")
    private String nickname;

    @ApiModelProperty(notes = "나이",example = "25")
    private Integer age;

    @ApiModelProperty(notes = "주소",example = "서울시/강서구")
    private Address address;

    @ApiModelProperty(notes = "작년 소득",example = "50000000")
    private Integer lastYearIncome;

    @ApiModelProperty(notes = "학력",example = "대학 재학")
    private EducationType educationType;

    @ApiModelProperty(notes = "직업",example = "재직자")
    private Set<JobType> jobs;

//    @ApiModelProperty(notes = "성별",example = "MALE")
//    private Gender gender;

    @ApiModelProperty(notes = "유저 상세 정보",example = "중소기업, 군인, 저소득층 ")
    private UserDetail userDetail;
}