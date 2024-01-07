package com.cmc.zenefitserver.domain.user.domain;

import lombok.Getter;

@Getter
public enum PrivacyType {

    TERMS_OF_SERVICE("이용약관","https://www.notion.so/zenefit/25528979c42847e2b6738ab7fd4edd33?pvs=4"),
    PRIVACY("개인정보처리방침","https://zenefit.notion.site/db4b51829a9e4be89c8ecbf6450215ac?pvs=4"),
    MARKETING("마켓팅정보","https://zenefit.notion.site/1fc76d68d52d43558858627b16f50c08?pvs=4");

    final String name;

    final String url;

    PrivacyType(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
