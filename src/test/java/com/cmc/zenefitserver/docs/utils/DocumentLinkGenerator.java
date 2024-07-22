package com.cmc.zenefitserver.docs.utils;

import com.cmc.zenefitserver.domain.user.domain.Gender;
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
        JOB_TYPE("jobType", "직업 코드"),
        GENDER("gender", "성별"),
        PROVIDER("providerType", "소셜 로그인 제공처"),
        CHARACTER("character", "유저 캐릭터"),
        SUPPORT_POLICY_TYPE("supportPolicyType", "지원 정책 유형"),
        POLICY_DATE_TYPE("policyDateType", "정책 신청 기간 타입"),
        POLICY_METHOD_TYPE("policyMethodType", "정책 신청 방법"),
        POLICY_TYPE("policyType", "정책 분야");

        private final String pageId;
        @Getter
        private final String text;


    }
}
