package com.cmc.zenefitserver.domain.policy.domain.enums;

import lombok.Getter;

@Getter
public enum PolicyCode {
    EMPLOYMENT_SUPPORT("004001", "취업지원"),
    STARTUP_SUPPORT("004002", "창업지원"),
    HOUSING_FINANCE("004003", "주거·금융"),
    LIFE_WELFARE("004004", "생활·복지"),
    POLICY_PARTICIPATION("004005", "정책참여");

    private final String code;
    private final String name;

    PolicyCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

}

