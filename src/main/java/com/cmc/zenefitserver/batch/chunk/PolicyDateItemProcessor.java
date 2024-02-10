package com.cmc.zenefitserver.batch.chunk;

import com.cmc.zenefitserver.batch.service.PolicyDateClassifier;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class PolicyDateItemProcessor implements ItemProcessor<Policy, Policy> {

    private final PolicyDateClassifier policyDateClassifier;

    @Override
    public Policy process(Policy item) throws Exception {
        policyDateClassifier.classify(item);
        return item;
    }
}
