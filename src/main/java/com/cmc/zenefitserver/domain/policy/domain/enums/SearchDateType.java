package com.cmc.zenefitserver.domain.policy.domain.enums;


public enum SearchDateType {
    STT_DATE("시작일"),
    END_DATE("마감일"),
    NONE("전체");

    final String name;

    SearchDateType(String name) {
        this.name = name;
    }
}
