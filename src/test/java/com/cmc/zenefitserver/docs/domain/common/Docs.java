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
    Map<String, String> genders;
    Map<String, String> providerTypes;
    Map<String, String> characters;
    Map<String, String> supportPolicyTypes;
    Map<String, String> policyDateTypes;
    Map<String, String> policyMethodTypes;
    Map<String, String> policyTypes;


    @Builder(builderClassName = "TestBuilder", builderMethodName = "testBuilder")
    public Docs(Map<String, String> errorCodes, Map<String, String> areaCodes, Map<String, String> cityCodes, Map<String, String> educationTypes, Map<String, String> jobTypes, Map<String, String> genders, Map<String, String> providerTypes, Map<String, String> characters, Map<String, String> supportPolicyTypes, Map<String, String> policyDateTypes, Map<String, String> policyMethodTypes, Map<String, String> policyTypes) {
        this.errorCodes = errorCodes;
        this.areaCodes = areaCodes;
        this.cityCodes = cityCodes;
        this.educationTypes = educationTypes;
        this.jobTypes = jobTypes;
        this.genders = genders;
        this.providerTypes = providerTypes;
        this.characters = characters;
        this.supportPolicyTypes = supportPolicyTypes;
        this.policyDateTypes = policyDateTypes;
        this.policyMethodTypes = policyMethodTypes;
        this.policyTypes = policyTypes;
    }
}
