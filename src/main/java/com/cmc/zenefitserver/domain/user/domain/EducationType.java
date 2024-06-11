package com.cmc.zenefitserver.domain.user.domain;


import com.cmc.zenefitserver.global.common.EnumType;
import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EducationType implements EnumType {
    BELOW_HIGH_SCHOOL("고졸 미만"), // 고졸 미만
    HIGH_SCHOOL_STUDENT("고교 재학"), // 고교 재학
    HIGH_SCHOOL_GRADUATION_EXPECTED("고졸 예정"), // 고졸 예정
    HIGH_SCHOOL_GRADUATE("고교 졸업"), // 고교 졸업
    COLLEGE_STUDENT("대학 재학"), // 대학 재학
    COLLEGE_GRADUATION_EXPECTED("대졸 예정"), // 대졸 예정
    COLLEGE_GRADUATE("대학 졸업"), // 대학 졸업
    POSTGRADUATE("석박사"), // 석·박사
    UNLIMITED("제한없음"); // 제한없음

    private final String description;

    EducationType(String description) {
        this.description = description;
    }

    @JsonCreator
    public static EducationType fromString(String value) {
        return Arrays.stream(EducationType.values())
                .filter(v -> v.description.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_EDUCATION_ENUM_VALUE));
    }

    @JsonValue
    public String toString() {
        return description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getId() {
        return this.name();
    }

    @Override
    public String getText() {
        return this.description;
    }
}