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



        Policy maxBenefitMoneyPolicy = recommendPolicy.stream()
                .filter(p -> p.getSupportPolicyType().equals(SupportPolicyType.MONEY))
                .max(Comparator.comparingInt(Policy::getBenefit))
                .orElse(null);

        Policy maxBenefitLoansPolicy = recommendPolicy.stream()
                .filter(p -> p.getSupportPolicyType().equals(SupportPolicyType.LOANS))
                .max(Comparator.comparingInt(Policy::getBenefit))
                .orElse(null);

        LocalDate now = LocalDate.now();
        Policy mostImminentEndDateSocialServicePolicy = recommendPolicy.stream()
                .filter(p -> p.getSupportPolicyType().equals(SupportPolicyType.SOCIAL_SERVICE))
                .filter(p -> p.getEndDate().isAfter(now))
                .min(Comparator.comparing(p -> ChronoUnit.DAYS.between(now, p.getEndDate())))
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

        return result;
    }

    public List<Policy> matchPolicy(User user) {
        // 1차 선별
        Integer age = user.getAge();

        AreaCode userAreaCode = user.getAddress().getCity();

        CityCode userCityCode = user.getAddress().getDistrict();

        List<Policy> firstFindPolices = policyRepository.findByAreaCodeAndCityCodeAndAge(userAreaCode, AreaCode.CENTRAL_GOVERNMENT, userCityCode, age);


        // 2차 선별
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
                                && !isEducationDenial(user, policy)
                                && !isLocalDenial(user, policy)

                )
                .collect(Collectors.toList());

        user.updatePolicyCnt(secondFindPolices.size());
        userRepository.save(user);
        return secondFindPolices;
    }

    public DenialReasonType getDenialReasonType(User user, Policy policy) {

        // 장애인 12
        if (isDisabledDenial(user, policy)) {
            return DenialReasonType.SPECIAL_CONTENT_DISABLED;
        }
        // 군인 11
        if (isSoldierDenial(user, policy)) {
            return DenialReasonType.SPECIAL_CONTENT_SOLDIER;
        }
        // 농업인 10
        if (isFarmerDenial(user, policy)) {
            return DenialReasonType.SPECIAL_CONTENT_FARMER;
        }
        // 여성 9
        if (isFarmerDenial(user, policy)) {
            return DenialReasonType.SPECIAL_CONTENT_FEMALE;
        }
        // 지역인재 8
        if (isLocalTalentDenial(user, policy)) {
            return DenialReasonType.SPECIAL_CONTENT_FARMER;
        }
        // 저소득 7
        if (isLowIncomeDenial(user, policy)) {
            return DenialReasonType.SPECIAL_CONTENT_LOW_INCOME;
        }
        // 중소기업 6
        if (isSmallBusinessDenial(user, policy)) {
            return DenialReasonType.SPECIAL_CONTENT_SMALL_BUSINESS;
        }
        // 나이 4
        if (isAgeDenial(user, policy)) {
            return DenialReasonType.AGE;
        }
        // 취업살태내용 3
        if (isJobDenial(user, policy)) {
            return DenialReasonType.JOB_CONTENT;
        }
        // 학력요건내용 2
        if (isEducationDenial(user, policy)) {
            return DenialReasonType.EDUCATION_CONTENT;
        }
        // 거주지역 1
        if (isLocalDenial(user, policy)) {
            return DenialReasonType.LOCAL;
        }
        return null;
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
        return userJobTypes.isEmpty();
    }

    private static boolean isEducationDenial(User user, Policy policy) {
        return !policy.getEducationTypes().contains(user.getEducationType());
    }

    private static boolean isLocalDenial(User user, Policy policy) {
        return user.getAddress().getCity() != policy.getAreaCode()
                || (policy.getCityCode() != null && policy.getCityCode() != user.getAddress().getDistrict());
    }
}
