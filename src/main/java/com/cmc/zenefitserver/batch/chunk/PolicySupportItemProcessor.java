package com.cmc.zenefitserver.batch.chunk;

//import com.cmc.zenefitserver.batch.service.PolicyDateClassifier;
import com.cmc.zenefitserver.batch.service.PolicySupportContentClassifier;
import com.cmc.zenefitserver.domain.policy.application.PolicyBenefitClassifier;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class PolicySupportItemProcessor implements ItemProcessor<Policy, Policy> {

    private final PolicySupportContentClassifier policySupportContentClassifier;
    private final PolicyBenefitClassifier policyBenefitClassifier;

    @Override
    public Policy process(Policy item) throws Exception {
        policySupportContentClassifier.getSupportContent(item);
        policyBenefitClassifier.saveBenefit(item);
        return item;
    }
}
