package com.cmc.zenefitserver.docs.domain.common;

import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import com.cmc.zenefitserver.domain.user.domain.EducationType;
import com.cmc.zenefitserver.domain.user.domain.JobType;
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

        return CommonResponse.success(
                Docs.testBuilder()
                        .errorCodes(errorCodes)
                        .areaCodes(areaCodes)
                        .cityCodes(cityCodes)
                        .educationTypes(educationTypes)
                        .jobTypes(jobTypes)
                        .build()
        );
    }

    private Map<String, String> getDocs(EnumType[] enumTypes) {
        return Arrays.stream(enumTypes)
                .collect(Collectors.toMap(EnumType::getId, EnumType::getText));

    }
}
