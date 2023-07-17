package com.cmc.zenefitserver.domain.user.domain;

public enum Gender {
    MALE("M"),
    FEMALE("W");

    private String code;

    Gender(String code) {
        this.code = code;
    }
}
