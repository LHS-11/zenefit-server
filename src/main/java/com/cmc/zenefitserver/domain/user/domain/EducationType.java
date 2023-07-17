package com.cmc.zenefitserver.domain.user.domain;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EducationType {
    BELOW_HIGH_SCHOOL("고졸 미만"), // 고졸 미만
    HIGH_SCHOOL_STUDENT("고교 재학"), // 고교 재학
    HIGH_SCHOOL_GRADUATION_EXPECTED("고졸 예정"), // 고졸 예정
    HIGH_SCHOOL_GRADUATE("고교 졸업"), // 고교 졸업
    COLLEGE_STUDENT("대학 재학"), // 대학 재학
    COLLEGE_GRADUATION_EXPECTED("대졸 예정"), // 대졸 예정
    COLLEGE_GRADUATE("대학 졸업"), // 대학 졸업
    POSTGRADUATE("석_박사"), // 석·박사
    UNLIMITED("제한없음"); // 제한없음

    private final String description;

    EducationType(String description) {
        this.description = description;
    }

    @JsonCreator
    public static EducationType fromString(String value) {
        for (EducationType educationType : EducationType.values()) {
            if (educationType.description.equalsIgnoreCase(value)) {
                return educationType;
            }
        }
        throw new IllegalArgumentException("Invalid EducationType value: " + value);
    }

    @JsonValue
    public String toString() {
        return description;
    }

    public String getDescription() {
        return description;
    }
}