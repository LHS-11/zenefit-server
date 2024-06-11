package com.cmc.zenefitserver.docs.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface DocumentLinkGenerator {

    static String generateLinkCode(DocUrl docUrl) {
        return String.format("link:common/%s.html[%s,role=\"popup\"]", docUrl.pageId, docUrl.text);
    }

    static String generateText(DocUrl docUrl) {
        return String.format("%s %s", docUrl.text, "코드명");
    }

    @RequiredArgsConstructor
    enum DocUrl {
        // ENUM 목록
        AREA_CODE("areaCode", "시/도 코드"),
        CITY_CODE("cityCode", "시/구 코드"),
        EDUCATION_TYPE("educationType", "학력 코드"),
        JOB_TYPE("jobType", "직업 코드");

        private final String pageId;
        @Getter
        private final String text;


    }
}
