package com.cmc.zenefitserver.docs.domain.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 공통 코드 객체화 문서
 */
@Getter
@NoArgsConstructor
public class Docs {

    Map<String, String> errorCodes;
    Map<String, String> areaCodes;
    Map<String, String> cityCodes;
    Map<String, String> educationTypes;
    Map<String, String> jobTypes;

    @Builder(builderClassName = "TestBuilder", builderMethodName = "testBuilder")
    public Docs(Map<String, String> errorCodes, Map<String, String> areaCodes, Map<String, String> cityCodes, Map<String, String> educationTypes, Map<String, String> jobTypes) {
        this.errorCodes = errorCodes;
        this.areaCodes = areaCodes;
        this.cityCodes = cityCodes;
        this.educationTypes = educationTypes;
        this.jobTypes = jobTypes;
    }
}
