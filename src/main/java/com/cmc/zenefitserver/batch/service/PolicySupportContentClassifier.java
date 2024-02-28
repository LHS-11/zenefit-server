package com.cmc.zenefitserver.batch.service;


import com.cmc.zenefitserver.domain.policy.application.PolicyContentFeignService;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import com.cmc.zenefitserver.domain.policy.dto.PolicyContentFeignDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PolicySupportContentClassifier {

    private final PolicyContentFeignService policyContentFeignService;

    public void getSupportContent(Policy policy) {

        // 100개씩 데이터를 쪼개어 처리
        try {
            PolicyContentFeignDto policyContentFeignDto = policyContentFeignService.getSupportContent(new URI("http://127.0.0.1:8000/predict"), policy.getSupportContent());
//            System.out.println("policy.getSupportContent() = " + policy.getSupportContent());
//            System.out.println("policyContentFeignDto.getResult() = " + policyContentFeignDto);
            String[] parts = policyContentFeignDto.getResult().split(",");
            if (policyContentFeignDto.getResult().equals("0")) {
                System.out.println(policy.getId());
            }
            Set<SupportPolicyType> supportPolicyTypes = Arrays.stream(parts)
                    .map(String::trim)
                    .filter(part -> !part.equals("0"))
                    .map(SupportPolicyType::findSupportPolicyTypeByOrder)
                    .collect(Collectors.toSet());

            supportPolicyTypes.removeAll(policy.getSupportPolicyTypes());

            policy.updateSupportTypes(supportPolicyTypes);

            // 여기에서 추가적인 처리를 수행합니다.
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

}
