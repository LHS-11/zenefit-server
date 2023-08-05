package com.cmc.zenefitserver.domain.policy.application;

import com.cmc.zenefitserver.domain.policy.domain.enums.PolicySplzType;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PolicySplzClassifier {

    public Set<PolicySplzType> mapToSplzCodeFromSplzContent(String splzContent) {
        Set<PolicySplzType> splzContentSplzCodes = new HashSet<>();
        if (splzContent.contains("여성")) {
            splzContentSplzCodes.add(PolicySplzType.FEMALE);
        }
        if (splzContent.contains("중소")) {
            splzContentSplzCodes.add(PolicySplzType.SMALL_BUSINESS);
        }
        if (splzContent.contains("군인") || splzContent.contains("장병")) {
            splzContentSplzCodes.add(PolicySplzType.SOLDIER);
        }
        if (splzContent.contains("저소득층") || splzContent.contains("기초생활수급자") || splzContent.contains("차상위 계층") || splzContent.contains("저소득 청년층")) {
            splzContentSplzCodes.add(PolicySplzType.LOW_INCOME);
        }
        if (splzContent.contains("장애인") || splzContent.contains("정신건강취약계층")) {
            splzContentSplzCodes.add(PolicySplzType.DISABLED);
        }
        if (splzContent.contains("지역 인재") || splzContent.contains("지역인재")) {
            splzContentSplzCodes.add(PolicySplzType.LOCAL_TALENT);
        }
        if (splzContent.contains("농업인") || splzContent.contains("농업") || splzContent.contains("창업농") || splzContent.contains("농어업") || splzContent.contains("영농종사자")) {
            splzContentSplzCodes.add(PolicySplzType.FARMER);
        }
        if (splzContent.contains("제한없음") || splzContent.contains("null") || splzContent.contains("제한 없음")) {
            splzContentSplzCodes.add(PolicySplzType.UNLIMITED);
        }
        if (splzContent.contains("창업자") || splzContent.contains("스타트업") || splzContent.contains("예비창업자")
                || splzContent.contains("초기 청년창업기업") || splzContent.contains("벤처 확인 기업") || splzContent.contains("위탁연구기관")
                || splzContent.contains("창업") || splzContent.contains("예비청년상인") || splzContent.contains("소상공인")) {
            splzContentSplzCodes.add(PolicySplzType.ENTREPRENEUR);
        }
        if (splzContent.contains("건설업") || splzContent.contains("제조업") || splzContent.contains("어선어업")
                || splzContent.contains("과학기술인") || splzContent.contains("문화 예술분야") || splzContent.contains("문화예술인") || splzContent.contains("수산업")) {
            splzContentSplzCodes.add(PolicySplzType.ETC);
        }
        return splzContentSplzCodes;

    }

}
