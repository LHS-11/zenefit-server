package com.cmc.zenefitserver.global.common.request;

import com.cmc.zenefitserver.domain.user.domain.User;
import lombok.Getter;

@Getter
public class TokenRequestDto {

    private final Long id;

    public TokenRequestDto(User user) {
        this.id = user.getUserId();
    }
}
