package com.cmc.zenefitserver.domain.userpolicy.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UserPolicyType {

    Favorite("관심정책"),
    BASIC("기본정책");

    private String description;

    UserPolicyType(String description) {
        this.description = description;
    }

    public static UserPolicyType findUserPolicyType(String code){
        return Arrays.stream(UserPolicyType.values())
                .filter(u -> u.getDescription().equals(code))
                .findFirst()
                .orElseGet(null);
    }
}
