package com.cmc.zenefitserver.batch.chunk;

import com.cmc.zenefitserver.batch.service.PolicyAgeClassifier;
import com.cmc.zenefitserver.batch.service.PolicyEduClassifier;
import com.cmc.zenefitserver.batch.service.PolicyEmpmClassifier;
import com.cmc.zenefitserver.batch.service.PolicySplzClassifier;
import com.cmc.zenefitserver.domain.policy.application.*;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.YouthPolicy;
import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class YouthPolicyItemProcessor implements ItemProcessor<YouthPolicy, Policy> {

    private final PolicyAgeClassifier policyAgeClassifier;
    private final PolicyEmpmClassifier policyEmpmClassifier;
    private final PolicyEduClassifier policyEduClassifier;
    private final PolicySplzClassifier policySplzClassifier;
    private final PolicyMethodClassifier policyMethodClassifier;
    private final PolicyRepository policyRepository;

    @Override
    public Policy process(YouthPolicy item) throws Exception {


        List<Policy> policies = policyRepository.findAll();
//        List<String> bizIds = policyRepository.findAll().stream().map(policy -> policy.getBizId()).collect(Collectors.toList());

        if (item == null) {
            log.debug("YouthPolicy 가 null 입니다.");
            return null;
        }

        for (Policy policy : policies) {
            if (policy.getBizId().equals(item.getBizId())) {
                policy.updateYouthPolicyInfo(item);
                if (item.getCityCode() != null) policy.updateCityCode(item.getCityCode());
                policy.updateAreaCode(item.getAreaCode());
                policy.updateJobTypes(policyEmpmClassifier.mapToJobTypesFromEmpmContent(item.getEmpmSttsCn()));
                policy.updateEducationTypes(policyEduClassifier.mapToEducationTypeFromEduContent(item.getAccrRqisCn()));
                policy.updateSplzTypes(policySplzClassifier.mapToSplzCodeFromSplzContent(item.getSplzRlmRqisCn()));
                policyAgeClassifier.setMaxAgeAndMinAge(policy);
//                policyMethodClassifier.classify(policy);
                return null;
            }
        }
        return mapSameBizIdYouthPolicyToPolicy(item);
    }

    private Policy mapSameBizIdYouthPolicyToPolicy(YouthPolicy youthPolicy) {

        Policy policy = Policy.builder()
                .bizId(youthPolicy.getBizId())
                .policyName(youthPolicy.getPolyBizSjnm())
                .policyIntroduction(youthPolicy.getPolyItcnCn())
                .operatingAgencyName(youthPolicy.getCnsgNmor())
                .applicationPeriodContent(youthPolicy.getRqutPrdCn())
                .organizationType(youthPolicy.getPolyBizTy())
                .supportContent(youthPolicy.getSporCn())
                .ageInfo(youthPolicy.getAgeInfo())
                .employmentStatusContent(youthPolicy.getEmpmSttsCn())
                .specializedFieldContent(youthPolicy.getSplzRlmRqisCn())
                .educationalRequirementContent(youthPolicy.getAccrRqisCn())
                .residentialAndIncomeRequirementContent(youthPolicy.getPrcpCn())
                .additionalClauseContent(youthPolicy.getAditRscn())
                .eligibilityTargetContent(youthPolicy.getPrcpLmttTrgtCn())
                .applicationSiteAddress(youthPolicy.getRqutUrla())
                .referenceSiteUrlAddress(youthPolicy.getRfcSiteUrla1())
                .applicationProcedureContent(youthPolicy.getRqutProcCn())
                .submissionDocumentContent(youthPolicy.getPstnPaprCn())
                .policyCode(PolicyCode.findPolicyCode(youthPolicy.getPolyRlmCd()))
                .build();

        if (youthPolicy.getCityCode() != null) policy.updateCityCode(youthPolicy.getCityCode());

        policy.updateAreaCode(youthPolicy.getAreaCode());
        policy.updateJobTypes(policyEmpmClassifier.mapToJobTypesFromEmpmContent(youthPolicy.getEmpmSttsCn()));
        policy.updateEducationTypes(policyEduClassifier.mapToEducationTypeFromEduContent(youthPolicy.getAccrRqisCn()));
        policy.updateSplzTypes(policySplzClassifier.mapToSplzCodeFromSplzContent(youthPolicy.getSplzRlmRqisCn()));
        policyAgeClassifier.setMaxAgeAndMinAge(policy);
//        policyMethodClassifier.classify(policy);
        return policy;
    }
}
