package com.cmc.zenefitserver.docs.domain.common;

import com.cmc.zenefitserver.domain.policy.domain.enums.*;
import com.cmc.zenefitserver.domain.user.domain.Character;
import com.cmc.zenefitserver.domain.user.domain.EducationType;
import com.cmc.zenefitserver.domain.user.domain.Gender;
import com.cmc.zenefitserver.domain.user.domain.JobType;
import com.cmc.zenefitserver.global.auth.ProviderType;
import com.cmc.zenefitserver.global.common.CommonResponse;
import com.cmc.zenefitserver.global.common.EnumType;
import com.cmc.zenefitserver.global.error.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class EnumViewController {

    @GetMapping("/docs")
    public CommonResponse<Docs> findAll() {

        Map<String, String> errorCodes = getDocs(ErrorCode.values());
        Map<String, String> areaCodes = getDocs(AreaCode.values());
        Map<String, String> cityCodes = getDocs(CityCode.values());
        Map<String, String> educationTypes = getDocs(EducationType.values());
        Map<String, String> jobTypes = getDocs(JobType.values());
        Map<String, String> genders = getDocs(Gender.values());
        Map<String, String> providerTypes = getDocs(ProviderType.values());
        Map<String, String> characters = getDocs(Character.values());
        Map<String, String> supportPolicyTypes = getDocs(SupportPolicyType.values());
        Map<String, String> policyDateTypes = getDocs(PolicyDateType.values());
        Map<String, String> policyMethodTypes = getDocs(PolicyMethodType.values());
        Map<String, String> policyTypes = getDocs(PolicyCode.values());

        return CommonResponse.success(
                Docs.testBuilder()
                        .errorCodes(errorCodes)
                        .areaCodes(areaCodes)
                        .cityCodes(cityCodes)
                        .educationTypes(educationTypes)
                        .jobTypes(jobTypes)
                        .genders(genders)
                        .providerTypes(providerTypes)
                        .characters(characters)
                        .supportPolicyTypes(supportPolicyTypes)
                        .policyDateTypes(policyDateTypes)
                        .policyMethodTypes(policyMethodTypes)
                        .policyTypes(policyTypes)
                        .build()
        );
    }

    private Map<String, String> getDocs(EnumType[] enumTypes) {
        return Arrays.stream(enumTypes)
                .collect(Collectors.toMap(EnumType::getId, EnumType::getText));

    }
}
