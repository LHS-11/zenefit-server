package com.cmc.zenefitserver.domain.policy.domain.enums;

import lombok.Getter;

@Getter
public enum DenialReasonType {

    AGE("나이","나이가 해당되지 않아요"),
    INCOME("소득","소득이 지원 조건보다 더 높아요"),
    LOCAL("거주 지역","다른 지역에서 지원하는 정책이네요"),
    EDUCATION_CONTENT("학력요건내용","학력요건이 맞지 않아요"),
    JOB_CONTENT("취업요건내용","직장 정보가 맞지 않아요"),
    SPECIAL_CONTENT_SMALL_BUSINESS("특화분야내용 - 중소기업","중소기업에 다니시는 분들만 신청 가능해요"),
    SPECIAL_CONTENT_FEMALE("특화분야내용 - 여성","이 정책은 여성분들만 신청 가능해요"),
    SPECIAL_CONTENT_SOLDIER("특화분야내용 - 군인","이 정책은 군인분들만 신청 가능해요"),
    SPECIAL_CONTENT_DISABLED("특화분야내용 - 장애인","이 정책은 장애인분들만 신청 가능해요"),
    SPECIAL_CONTENT_LOCAL_TALENT("특화분야내용 - 지역인재","지역인재에 해당하는 분들만 신청 가능해요"),
    SPECIAL_CONTENT_FARMER("특화분야내용 - 농업인","이 정책은 농업인분들만 신청 가능해요"),
    SPECIAL_CONTENT_LOW_INCOME("특화분야내용 - 저소득층","저소득층에 해당하는 분들만 신청 가능해요"),
    SPECIAL_CONTENT_ENTREPRENEUR("특화분야내용 - 창업가","창업가 해당하는 분들만 신청 가능해요");

    private final String name;
    private final String Text;

    DenialReasonType(String name, String text) {
        this.name = name;
        Text = text;
    }
}
