package com.cmc.zenefitserver.global.auth.kakao;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class KakaoAccount {
    private String gender;
    private String email;
    private Profile profile;

    @Getter
    public static class Profile{
        private String nickname;
    }
}