package com.cmc.zenefitserver.domain.policy.application;

import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.DenialReasonType;
import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyDateType;
import com.cmc.zenefitserver.domain.policy.domain.enums.PolicySplzType;
import com.cmc.zenefitserver.domain.user.domain.EducationType;
import com.cmc.zenefitserver.domain.user.domain.Gender;
import com.cmc.zenefitserver.domain.user.domain.JobType;
import com.cmc.zenefitserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class PolicyDenialReasonClassifier {

    public static DenialReasonType getDenialReasonType(User user, Policy policy) {

        // 신청 기간 X
        if (isDateDenial(user, policy)) {
            return DenialReasonType.DATE;
        }
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
        if (isFemaleDenial(user, policy)) {
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

    private static boolean isDateDenial(User user, Policy policy) {
        LocalDate now = LocalDate.now();
        // 1. 현재 시각이 신청 시작일보다 이른 경우  or 2. 현재 시각이 신청 종료일보다 늦은 경우
        if (policy.getPolicyDateType() != null && policy.getPolicyDateType().equals(PolicyDateType.PERIOD)
                && ((policy.getApplyEndDate() != null && now.isAfter(policy.getApplyEndDate()))
                || (policy.getApplySttDate() != null && now.isBefore(policy.getApplySttDate()))
                || (policy.getApplySttDate() == null && policy.getApplyEndDate() == null))) {
            return true;
        }
        return false;
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
        userJobTypes.contains(policyJobTypes);
//        userJobTypes.retainAll(policyJobTypes);
        return Collections.disjoint(userJobTypes, policyJobTypes) && (!(policy.getJobTypes().contains(JobType.UNLIMITED) || policy.getJobTypes() == null));
    }

    private static boolean isEducationDenial(User user, Policy policy) {
        return !policy.getEducationTypes().contains(user.getEducationType()) && !policy.getEducationTypes().contains(EducationType.UNLIMITED);
    }

    /**
     * 1. 정책의 지역코드가 중앙부처가 아니어야함
     * 1-1. 정책의 지역코드와 유저의 지역코드가 틀려야함
     * 1-2. 정책의
     */
    private static boolean isLocalDenial(User user, Policy policy) {
        return !policy.getAreaCode().equals(AreaCode.CENTRAL_GOVERNMENT)
                && (user.getAddress().getAreaCode() != policy.getAreaCode()
                || (policy.getCityCode() != null && policy.getCityCode() != user.getAddress().getCityCode()));
    }
}
