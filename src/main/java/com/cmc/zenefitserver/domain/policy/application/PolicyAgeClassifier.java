package com.cmc.zenefitserver.domain.policy.application;

import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PolicyAgeClassifier {

    private final PolicyRepository policyRepository;
    private EntityManager em;

    public void setMaxAgeAndMinAge(Policy policy) {
        String ageInfo = policy.getAgeInfo();

        int minAge=0; // 이상
        int maxAge=120; // 이하

        if(ageInfo.contains("이상")){
            String parsingNumber = ageInfo.replaceAll("[^0-9]", "");
            minAge = Integer.parseInt(parsingNumber);
        }
        if(ageInfo.contains("이하")){
            String parsingNumber = ageInfo.replaceAll("[^0-9]", "");
            maxAge = Integer.parseInt(parsingNumber);
        }
        if(ageInfo.contains("~")){
            String[] parsingNumbers = ageInfo.split("~");
            minAge = Integer.parseInt(parsingNumbers[0].replaceAll("[^0-9]", ""));
            maxAge = Integer.parseInt(parsingNumbers[1].replaceAll("[^0-9]", ""));
        }

        policy.updateAgeInfo(minAge, maxAge);
    }

}
