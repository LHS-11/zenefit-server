package com.cmc.zenefitserver.domain.policy.domain.enums;

import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum PolicySplzType {

    FEMALE("여성"),
    SMALL_BUSINESS("중소기업"),
    SOLDIER("군인"),
    LOW_INCOME("저소득층"),
    DISABLED("장애인"),
    LOCAL_TALENT("지역인재"),
    FARMER("농업인"),
    UNLIMITED("제한없음"),
    ETC("기타"),
    ENTREPRENEUR("창업자");
    private final String description;


    PolicySplzType(String description) {
        this.description = description;
    }

    @JsonCreator
    public static PolicySplzType fromString(String value) {
        return Arrays.stream(PolicySplzType.values())
                .filter(v -> v.description.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_SPLZ_ENUM_VALUE));
    }

}
