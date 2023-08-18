package com.cmc.zenefitserver.domain.policy.domain.enums;

import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

import static com.cmc.zenefitserver.global.error.ErrorCode.NOT_FOUND_SUPPORT_POLICY_TYPE_ENUM_VALUE;

@Getter
public enum SupportPolicyType {

    MONEY("현금","money"),
    LOANS("대출","loan"),
    SOCIAL_SERVICE("사회복지","social service");

    private String description;
    private String name;

    SupportPolicyType(String description, String name) {
        this.description = description;
        this.name = name;
    }

    public static SupportPolicyType fromString(String value){
        return Arrays.stream(SupportPolicyType.values())
                .filter(s -> s.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_FOUND_SUPPORT_POLICY_TYPE_ENUM_VALUE));
    }

    public static SupportPolicyType findSupportPolicyTypeContains(String name){
        return Arrays.stream(SupportPolicyType.values())
                .filter(s -> name.contains(s.getName()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_FOUND_SUPPORT_POLICY_TYPE_ENUM_VALUE));
    }
}
