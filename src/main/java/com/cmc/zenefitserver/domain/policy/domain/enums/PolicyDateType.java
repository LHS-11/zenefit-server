package com.cmc.zenefitserver.domain.policy.domain.enums;


import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.Getter;

@Getter
public enum PolicyDateType {

    CONSTANT("상시"),
    UNDECIDED("미정"),
    PERIOD("기간 신청"),
    BLANK("빈값");

    private final String description;

    PolicyDateType(String description) {
        this.description = description;
    }

//    public PolicyDateType findPolicyTypeByDescription(String description){
//        return Arrays.stream(PolicyDateType.values())
//                .filter(p -> p.description.equals(description))
//                .findFirst()
//                .orElseThrow(()->new BusinessException(NOT_FOUND_POLICY_DATE_TYPE));
//    }
}
