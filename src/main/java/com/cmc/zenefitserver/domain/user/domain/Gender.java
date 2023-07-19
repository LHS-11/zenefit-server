package com.cmc.zenefitserver.domain.user.domain;

import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE");

    private String description;

    Gender(String description) {
        this.description = description;
    }

    @JsonCreator
    public static Gender fromString(String value) {
        return Arrays.stream(Gender.values())
                .filter(v -> v.description.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(()->new BusinessException(ErrorCode.NOT_FOUND_GENDER_ENUM_VALUE));
    }

    @JsonValue
    public String toString() {
        return description;
    }
}
