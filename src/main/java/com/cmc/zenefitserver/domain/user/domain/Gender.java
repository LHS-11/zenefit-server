package com.cmc.zenefitserver.domain.user.domain;

import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Gender {
    MALE("남성", "m", "character/m"),
    FEMALE("여성", "f", "character/f");

    private String description;
    private String code;
    private String url;

    Gender(String description, String code, String url) {
        this.description = description;
        this.code = code;
        this.url = url;
    }

    @JsonCreator
    public static Gender fromString(String value) {
        return Arrays.stream(Gender.values())
                .filter(v -> v.description.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GENDER_ENUM_VALUE));
    }

    public static Gender fromStringToName(String value) {
        return Arrays.stream(Gender.values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GENDER_ENUM_VALUE));
    }

    @JsonValue
    public String toString() {
        return description;
    }
}
