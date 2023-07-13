package com.cmc.zenefitserver.global.common.request;

import lombok.Getter;

@Getter
public class TokenRequestDto {

    private final Long id;

    public TokenRequestDto(Long id) {
        this.id = id;
    }
}
