package com.cmc.zenefitserver.domain.policy.domain.enums;

import com.cmc.zenefitserver.global.common.EnumType;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

import static com.cmc.zenefitserver.global.error.ErrorCode.NOT_FOUND_SUPPORT_POLICY_TYPE_ENUM_VALUE;

@Getter
public enum SupportPolicyType implements EnumType {

    MONEY("현금", "MONEY", "1"),
    LOANS("대출", "LOANS", "2"),
    SOCIAL_SERVICE("사회복지", "SOCIAL_SERVICE", "3");

    private String description;
    private String name;
    private String order;

    SupportPolicyType(String description, String name, String order) {
        this.description = description;
        this.name = name;
        this.order = order;
    }

    public static SupportPolicyType fromString(String value) {
        return Arrays.stream(SupportPolicyType.values())
                .filter(s -> s.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_FOUND_SUPPORT_POLICY_TYPE_ENUM_VALUE));
    }

    public static SupportPolicyType findSupportPolicyTypeContains(String name) {
        return Arrays.stream(SupportPolicyType.values())
                .filter(s -> name.contains(s.getName()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_FOUND_SUPPORT_POLICY_TYPE_ENUM_VALUE));
    }

    public static SupportPolicyType findSupportPolicyTypeByOrder(String order) {
        return Arrays.stream(SupportPolicyType.values())
                .filter(s -> s.order.equals(order))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_FOUND_SUPPORT_POLICY_TYPE_ENUM_VALUE));
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
