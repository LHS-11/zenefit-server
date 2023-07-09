package com.cmc.zenefitserver.domain.user.domain;

public enum Gender {
    MAN("M"),
    WOMAN("W");

    private String code;

    Gender(String code) {
        this.code = code;
    }
}
