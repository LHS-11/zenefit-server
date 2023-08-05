package com.cmc.zenefitserver.domain.policy.application;

import com.cmc.zenefitserver.domain.user.domain.JobType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class PolicyEmpmClassifier {

    public Set<JobType> mapToJobTypesFromEmpmContent(String empmContent) {

        Set<JobType> empmContentJobTypes = new HashSet<>();
        if (empmContent.contains("재직자") || empmContent.contains("사회초년생") || (empmContent.contains("취업자") && empmContent.contains("미취업자")) || empmContent.contains("재직근로자")
                || empmContent.contains("근로/사업소득 월 50만 원 초과~220만 원 이하") || empmContent.contains("취업비자 취득, 단순 노무직종 제외, 연봉 1,700만원 이상, 근로계약기간 1년 이상, 취업 업체(다국적기업 등)")
                || empmContent.contains("중소기업 근로자")) {
            empmContentJobTypes.add(JobType.EMPLOYED);
        }
        if (empmContent.contains("자영업자") || empmContent.contains("소상공인") || empmContent.contains("근로/사업소득 월 50만 원 초과~220만 원 이하")) {
            empmContentJobTypes.add(JobType.SELF_EMPLOYED);
        }
        if (empmContent.contains("미취업") || empmContent.contains("대학생") || empmContent.contains("취업준비생") || empmContent.contains("청년 구직자") || empmContent.contains("구직단념청년")
                || empmContent.contains("채용예정자") || empmContent.contains("구직급여 수급자") || empmContent.contains("정규직채용일로부터 6개월 이내")
                || empmContent.contains("산업체의 직원으로 소속되어 있지 않은 자")) {
            empmContentJobTypes.add(JobType.UNEMPLOYED);
        }
        if (empmContent.contains("프리랜서")) {
            empmContentJobTypes.add(JobType.FREELANCER);
        }
        if (empmContent.contains("일용근로자")) {
            empmContentJobTypes.add(JobType.DAILY_WORKER);
        }
        if (empmContent.contains("창업자") || empmContent.contains("창업기업") || empmContent.contains("창업") || empmContent.contains("근로/사업소득 월 50만 원 초과~220만 원 이하")) {
            empmContentJobTypes.add(JobType.ENTREPRENEUR);
        }
        if (empmContent.contains("단기근로자")) {
            empmContentJobTypes.add(JobType.SHORT_TERM_WORKER);
        }
        if (empmContent.contains("영농종사자") || empmContent.contains("영농")) {
            empmContentJobTypes.add(JobType.FARMER);
        }
        if (empmContent.contains("제한") || empmContent.contains("-") || empmContent.contains("null")) {
            empmContentJobTypes.add(JobType.UNLIMITED);
        }
        return empmContentJobTypes;
    }
}

