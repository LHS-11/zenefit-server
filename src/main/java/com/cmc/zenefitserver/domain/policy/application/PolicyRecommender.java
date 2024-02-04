package com.cmc.zenefitserver.domain.policy.application;


import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.*;
import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.domain.user.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PolicyRecommender {


    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<SupportPolicyType, Policy> recommendPolicy(User user) {

        Map<SupportPolicyType, Policy> result = new HashMap<>();

        List<Policy> recommendPolicy = matchPolicy(user);

        System.out.println("recommendPolicy.size() = " + recommendPolicy.size());
        LocalDate now = LocalDate.now();

        Policy maxBenefitMoneyPolicy = recommendPolicy.stream()
                .filter(p -> p.getSupportPolicyTypes().contains((SupportPolicyType.MONEY)))
                .filter(p -> p.getApplySttDate() == null || now.isBefore(p.getApplyEndDate()))
                .max(Comparator.comparing(Policy::getBenefit))
                .orElse(null);

        Policy maxBenefitLoansPolicy = recommendPolicy.stream()
                .filter(p -> p.getSupportPolicyTypes().contains(SupportPolicyType.LOANS))
                .filter(p -> p.getApplySttDate() == null || now.isBefore(p.getApplyEndDate()))
                .max(Comparator.comparing(Policy::getBenefit))
                .orElse(null);

        Policy mostImminentEndDateSocialServicePolicy = recommendPolicy.stream()
                .filter(p -> p.getSupportPolicyTypes().contains(SupportPolicyType.SOCIAL_SERVICE))
                .filter(p -> p.getApplyEndDate().isAfter(now))
                .min(Comparator.comparing(p -> ChronoUnit.DAYS.between(now, p.getApplyEndDate())))
                .orElse(null);

        System.out.println("mostImminentEndDateSocialServicePolicy = " + mostImminentEndDateSocialServicePolicy);
        System.out.println("maxBenefitLoansPolicy = " + maxBenefitLoansPolicy);
        System.out.println("maxBenefitMoneyPolicy = " + maxBenefitMoneyPolicy);

        if (maxBenefitMoneyPolicy != null) {
            result.put(SupportPolicyType.MONEY, maxBenefitMoneyPolicy);
        }
        if (maxBenefitLoansPolicy != null) {
            result.put(SupportPolicyType.LOANS, maxBenefitLoansPolicy);
        }
        if (mostImminentEndDateSocialServicePolicy != null) {
            result.put(SupportPolicyType.SOCIAL_SERVICE, mostImminentEndDateSocialServicePolicy);
        }

        if (mostImminentEndDateSocialServicePolicy == null) {
            mostImminentEndDateSocialServicePolicy = recommendPolicy.stream()
                    .filter(p -> p.getSupportPolicyTypes().contains(SupportPolicyType.SOCIAL_SERVICE))
                    .findFirst()
                    .orElse(null);
            result.put(SupportPolicyType.SOCIAL_SERVICE, mostImminentEndDateSocialServicePolicy);
        }

        return result;
    }

    public List<Policy> matchPolicy(User user) {
        // 1차 선별 - 나이, 지역
        Integer age = user.getAge();

        AreaCode userAreaCode = user.getAddress().getAreaCode();

        CityCode userCityCode = user.getAddress().getCityCode();

        List<Policy> firstFindPolices = policyRepository.findByAreaCodeAndCityCodeAndAge(userAreaCode, AreaCode.CENTRAL_GOVERNMENT, userCityCode, age);

        // 2차 선별 - 특화 유형 ( policySplzTypes )
        List<Policy> secondFindPolices = firstFindPolices.stream()
                .filter(policy ->
                        !isDisabledDenial(user, policy)
                                && !isSoldierDenial(user, policy)
                                && !isFarmerDenial(user, policy)
                                && !isFemaleDenial(user, policy)
                                && !isLocalTalentDenial(user, policy)
                                && !isLowIncomeDenial(user, policy)
                                && !isSmallBusinessDenial(user, policy)
                                && !isEducationDenial(user, policy)
                                && !isAgeDenial(user, policy)
                                && !isJobDenial(user, policy)
                                && !isLocalDenial(user, policy)

                )
                .collect(Collectors.toList());

        user.updatePolicyCnt(secondFindPolices.size());
        userRepository.save(user);
        return secondFindPolices;
    }


    private static boolean isDisabledDenial(User user, Policy policy) {
        return !user.getUserDetail().isDisabled() && policy.getPolicySplzTypes().contains(PolicySplzType.DISABLED);
    }

    private static boolean isSoldierDenial(User user, Policy policy) {
        return !user.getUserDetail().isSoldier() && policy.getPolicySplzTypes().contains(PolicySplzType.SOLDIER);
    }

    private static boolean isFarmerDenial(User user, Policy policy) {
        return !user.getUserDetail().isFarmer() && policy.getPolicySplzTypes().contains(PolicySplzType.FARMER);
    }

    private static boolean isFemaleDenial(User user, Policy policy) {
        return !user.getUserDetail().getGender().equals(Gender.FEMALE) && policy.getPolicySplzTypes().contains(PolicySplzType.FEMALE);
    }

    private static boolean isLocalTalentDenial(User user, Policy policy) {
        return !user.getUserDetail().isLocalTalent() && policy.getPolicySplzTypes().contains(PolicySplzType.LOCAL_TALENT);
    }

    private static boolean isLowIncomeDenial(User user, Policy policy) {
        return !user.getUserDetail().isLowIncome() && policy.getPolicySplzTypes().contains(PolicySplzType.LOW_INCOME);

    }

    private static boolean isSmallBusinessDenial(User user, Policy policy) {
        return !user.getUserDetail().isSmallBusiness() && policy.getPolicySplzTypes().contains(PolicySplzType.SMALL_BUSINESS);
    }

    private static boolean isAgeDenial(User user, Policy policy) {
        return !(user.getAge() >= policy.getMinAge() && user.getAge() <= policy.getMaxAge());
    }

    private static boolean isJobDenial(User user, Policy policy) {
        Set<JobType> policyJobTypes = policy.getJobTypes();
        Set<JobType> userJobTypes = user.getJobs();
        userJobTypes.retainAll(policyJobTypes);
        return userJobTypes.isEmpty() && !(policy.getJobTypes().contains(JobType.UNLIMITED));
    }

    private static boolean isEducationDenial(User user, Policy policy) {
        return !policy.getEducationTypes().contains(user.getEducationType()) && !policy.getEducationTypes().contains(EducationType.UNLIMITED);
    }

    private static boolean isLocalDenial(User user, Policy policy) {
        return user.getAddress().getAreaCode() != policy.getAreaCode()
                || (policy.getCityCode() != null && policy.getCityCode() != user.getAddress().getCityCode());
    }
}
