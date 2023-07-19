package com.cmc.zenefitserver.domain.user.domain;

import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum JobType {

    EMPLOYED("재직자"), // 재직자
    SELF_EMPLOYED("자영업자"), // 자영업자
    UNEMPLOYED("미취업자"), // 미취업자
    FREELANCER("프리랜서"), // 프리랜서
    DAILY_WORKER("일용근로자"), // 일용근로자
    ENTREPRENEUR("창업자"), // (예비) 창업자
    SHORT_TERM_WORKER("단기근로자"), // 단기근로자
    FARMER("영농종사자"), // 영농종사자
    UNLIMITED("제한없음"); // 제한없음

    private final String description;


    JobType(String description) {
        this.description = description;
    }

    @JsonCreator
    public static JobType fromString(String value) {
        return Arrays.stream(JobType.values())
                .filter(v -> v.description.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_JOB_ENUM_VALUE));
    }
}
