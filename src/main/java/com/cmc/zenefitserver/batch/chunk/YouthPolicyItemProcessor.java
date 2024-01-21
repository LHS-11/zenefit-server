package com.cmc.zenefitserver.batch.chunk;

import com.cmc.zenefitserver.domain.policy.application.*;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.YouthPolicy;
import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
@RequiredArgsConstructor
public class YouthPolicyItemProcessor implements ItemProcessor<YouthPolicy, Policy> {

    private final PolicyAgeClassifier policyAgeClassifier;
    private final PolicyEmpmClassifier policyEmpmClassifier;
    private final PolicyEduClassifier policyEduClassifier;
    private final PolicySplzClassifier policySplzClassifier;
    private final PolicyMethodClassifier policyMethodClassifier;

    @Override
    public Policy process(YouthPolicy item) throws Exception {
        if (item == null) {
            log.debug("YouthPolicy 가 null 입니다.");
            return null;
        }

        return mapYouthPolicyToPolicy(item);
    }

    private Policy mapYouthPolicyToPolicy(YouthPolicy youthPolicy) {

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
        policyMethodClassifier.classify(policy);
        policyAgeClassifier.setMaxAgeAndMinAge(policy);
        return policy;
    }
}
