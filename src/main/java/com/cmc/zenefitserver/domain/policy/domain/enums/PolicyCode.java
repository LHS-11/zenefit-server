package com.cmc.zenefitserver.domain.policy.domain.enums;

import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import java.util.Arrays;

@Getter
public enum PolicyCode {

    JOB("023010", "일자리 분야"),
    RESIDENCE("023020", "주거 분야"),
    EDUCATION("023030", "교육 분야"),
    WELFARE_CULTURE("023040", "복지.문화 분야"),
    PARTICIPATION_RIGHT("023050", "참여.권리 분야"),
    NONE("", "전체 분야");

    private final String code;
    private final String name;

    PolicyCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PolicyCode findPolicyCode(String code){
        return Arrays.stream(PolicyCode.values())
                .filter(p -> p.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("정책 유형을 찾을 수 없습니다."));
    }

    @JsonCreator
    public static PolicyCode fromString(String value){
        return Arrays.stream(PolicyCode.values())
                .filter(p -> p.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_POLICY_TYPE_ENUM_VALUE));
    }

}

