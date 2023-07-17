package com.cmc.zenefitserver.global.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderType {
    KAKAO("K"),
    APPLE("A"),
    GOOGLE("G"),
    NAVER("N");

    private String value;

}
