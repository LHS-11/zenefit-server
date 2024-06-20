package com.cmc.zenefitserver.domain.policy.domain.enums;

import com.cmc.zenefitserver.global.common.EnumType;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum PolicyMethodType implements EnumType {

    ONLINE("온라인신청", Arrays.asList("온라인", "누리집", "홈페이지", "포털", "블로그", "이메일", "모바일", "email", "e-mail", "e-메일", "팩스", "전화", "워크넷", "https", "www")),
    LETTER("우편신청", Arrays.asList("우편")),
    VISIT("방문신청", Arrays.asList("방문", "읍면", "사무소", "사무실", "센터", "관할", "내방")),
    BLANK("빈값", Collections.emptyList());

    private final String description;

    private final List<String> keywords;

    PolicyMethodType(String description, List<String> keywords) {
        this.description = description;
        this.keywords = keywords;
    }

    public static PolicyMethodType findPolicyMethodTypeByKeywords(String text) {
        return Arrays.stream(PolicyMethodType.values())
                .filter(policyMethodType -> policyMethodType.keywords.stream().anyMatch(text::contains))
                .findFirst()
                .orElse(BLANK);
    }

    @Override
    public String getId() {
        return name();
    }

    @Override
    public String getText() {
        return description;
    }
}
