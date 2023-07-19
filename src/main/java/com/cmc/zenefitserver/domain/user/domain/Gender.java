package com.cmc.zenefitserver.domain.user.domain;

import java.util.Arrays;

public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE");

    private String value;

    Gender(String value) {
        this.value = value;
    }
}
