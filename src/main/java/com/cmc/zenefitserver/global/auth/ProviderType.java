package com.cmc.zenefitserver.global.auth;

import com.cmc.zenefitserver.global.common.EnumType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderType implements EnumType {
    KAKAO("K"),
    APPLE("A"),
    GOOGLE("G"),
    NAVER("N");

    private String value;

    @Override
    public String getId() {
        return this.name();
    }

    @Override
    public String getText() {
        return this.value;
    }
}
