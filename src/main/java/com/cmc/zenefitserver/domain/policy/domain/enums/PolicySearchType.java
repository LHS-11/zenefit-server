package com.cmc.zenefitserver.domain.policy.domain.enums;

public enum PolicySearchType {

    SUPPORT("support"), // 지원유형
    POLICY("bizTycdSel"), // 정책유형
    AREA_CODE("srchPolyBizSecd"); // 지역코드

    private String parameter;

    PolicySearchType(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
