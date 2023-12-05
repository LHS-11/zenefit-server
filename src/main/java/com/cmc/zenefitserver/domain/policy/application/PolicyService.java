package com.cmc.zenefitserver.domain.policy.application;

import com.cmc.zenefitserver.domain.policy.dao.PolicyQueryRepository;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.DenialReasonType;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import com.cmc.zenefitserver.domain.policy.dto.*;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.user.dto.HomeInfoResponseDto;
import com.cmc.zenefitserver.domain.userpolicy.dao.UserPolicyRepository;
import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PolicyService {

    private final PolicyQueryRepository policyQueryRepository;
    private final PolicyRepository policyRepository;
    private final UserPolicyRepository userPolicyRepository;
    private final PolicyRecommender policyRecommender;

    // 정책 리스트 조회 비즈니스 로직
    public Page<PolicyListResponseDto> getPolicyList(User user, PolicyListRequestDto policyListRequestDto, int page, int size, Sort sort) {
        PageRequest pageable = PageRequest.of(page, size, sort);
        return policyQueryRepository.searchByPaging(user, policyListRequestDto.getSupportPolicyType(), policyListRequestDto.getPolicyType(), null, pageable)
                .map(dto -> {
                    Policy findPolicy = policyRepository.findById(dto.getPolicyId()).get();
                    dto.updatePolicyApplyDenialReason(PolicyDenialReasonClassifier.getDenialReasonType(user, findPolicy).getText());
                    dto.updatePolicyDateTypeDescription(dto.getPolicyDateType());
                    dto.updateAreaCode(dto.getAreaCode());
                    dto.updateCityCode(dto.getCityCode());
                    return dto;
                });
    }

    public Page<PolicyListResponseDto> getSearchPolicyList(User user, SearchPolicyListRequestDto searchPolicyDto, int page, int size, Sort sort) {
        PageRequest pageable = PageRequest.of(page, size, sort);
        return policyQueryRepository.searchByPaging(user, searchPolicyDto.getSupportPolicyType(), searchPolicyDto.getPolicyType(), searchPolicyDto.getKeyword(), pageable)
                .map(dto -> {
                    Policy findPolicy = policyRepository.findById(dto.getPolicyId()).get();
                    dto.updatePolicyApplyDenialReason(PolicyDenialReasonClassifier.getDenialReasonType(user, findPolicy).getText());
                    dto.updatePolicyDateTypeDescription(dto.getPolicyDateType());
                    dto.updateAreaCode(dto.getAreaCode());
                    dto.updateCityCode(dto.getCityCode());
                    return dto;
                });
    }

    public PolicyInfoResponseDto getPolicy(User user, Long policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_POLICY));

        // 신청 불가 사유 로직
        String denialReason = "null";

        PolicyInfoResponseDto dto = PolicyInfoResponseDto.builder()
                .policyId(policy.getId())
                .policyName(policy.getPolicyName())
                .policyIntroduction(policy.getPolicyIntroduction())
                .policyApplyDenialReason(PolicyDenialReasonClassifier.getDenialReasonType(user, policy).getText())
                .policyApplyDocument(policy.getSubmissionDocumentContent())
                .policyApplyMethod(policy.getApplicationProcedureContent())
                .policyApplyDate(policy.getApplicationPeriodContent())
                .policyDateType(policy.getPolicyDateType())
                .policyDateTypeDescription(policy.getPolicyDateType().getDescription())
                .applicationSite(policy.getApplicationSiteAddress())
                .referenceSite(policy.getReferenceSiteUrlAddress())
                .benefit(policy.getBenefit())
                .build();

        return dto;
    }

    public List<CalendarPolicyListResponseDto> getPolicyListBySearchDate(User user, LocalDate searchDate) {

        LocalDate searchSttDate = searchDate.withDayOfMonth(1);
        LocalDate searchEndDate = searchDate.withDayOfMonth(searchDate.lengthOfMonth());

        List<CalendarPolicyListResponseDto> result = null;

        List<Policy> polices = policyRepository.findAllBySearchDate(user.getUserId(), searchSttDate, searchEndDate);

        if (polices != null) {
            result = polices.stream()
                    .map(policy -> {
                        DenialReasonType denialReasonType = PolicyDenialReasonClassifier.getDenialReasonType(user, policy);
                        CalendarPolicyListResponseDto dto = CalendarPolicyListResponseDto.builder()
                                .policyId(policy.getId())
                                .policyName(policy.getPolicyName())
                                .applyProcedure("더미데이터 (방문신청, 우편신청, 홈페이지 신청등)")
                                .policyAgencyLogo(policy.getPolicyLogo())
                                .applySttDate(policy.getApplySttDate())
                                .applyEndDate(policy.getApplyEndDate())
                                .build();

                        dto.upgradeApplyStatus(denialReasonType);
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        return result;
    }

    public RecommendPolicyInfoResponseDto recommend(User user) {

        Map<SupportPolicyType, Policy> supportPolicyTypePolicyMap = policyRecommender.recommendPolicy(user);

        List<RecommendPolicyInfoResponseDto.recommendPolicyInfo> recommendPolicyInfos = supportPolicyTypePolicyMap.keySet()
                .stream()
                .map(supportPolicyType -> {
                    Policy policy = supportPolicyTypePolicyMap.get(supportPolicyType);
                    RecommendPolicyInfoResponseDto.recommendPolicyInfo dto = RecommendPolicyInfoResponseDto.recommendPolicyInfo.builder()
                            .policyId(policy.getId())
                            .policyName(policy.getPolicyName())
                            .policyLogo(policy.getPolicyLogo())
                            .policyAreaCode(policy.getAreaCode().getName())
                            .policyCityCode(policy.getCityCode().getName())
                            .policyIntroduction(policy.getPolicyIntroduction())
                            .supportTypePolicyCnt(policyRepository.getPolicyCntBySupportPolicyType(supportPolicyType))
                            .benefit(10000000)
                            .policyDateType(policy.getPolicyDateType().getDescription())
                            .build();
                    return dto;
                }).collect(Collectors.toList());

        return RecommendPolicyInfoResponseDto.builder().policyInfos(recommendPolicyInfos).build();
    }

    public List<HomeInfoResponseDto.HomePolicyInfo> recommendPolicy(User user) {
        Map<SupportPolicyType, Policy> supportPolicyTypePolicyMap = policyRecommender.recommendPolicy(user);

        return supportPolicyTypePolicyMap.keySet()
                .stream()
                .map(supportPolicyType -> {
                    Policy policy = supportPolicyTypePolicyMap.get(supportPolicyType);

                    HomeInfoResponseDto.HomePolicyInfo dto = HomeInfoResponseDto.HomePolicyInfo.builder()
                            .policyId(policy.getId())
                            .policyName(policy.getPolicyName())
                            .policyLogo(policy.getPolicyLogo())
                            .supportPolicyType(supportPolicyType)
                            .supportPolicyTypeName(supportPolicyType.getDescription())
                            .build();
                    return dto;
                }).collect(Collectors.toList());
    }

    public PolicyCountResponseDto getRecommendCountAndNickname(User user) {
        return PolicyCountResponseDto.builder()
                .nickname(user.getNickname())
                .policyCnt(policyRecommender.matchPolicy(user).size())
                .build();
    }

    public RecommendPolicyInfoResponseDto getRecommendPolicyDummy() {
        Policy loansPolicy = policyRepository.findAllBySupportPolicyType(SupportPolicyType.LOANS).get(0);
        Policy moneyPolicy = policyRepository.findAllBySupportPolicyType(SupportPolicyType.MONEY).get(0);
        Policy socialServicePolicy = policyRepository.findAllBySupportPolicyType(SupportPolicyType.SOCIAL_SERVICE).get(0);

        List<RecommendPolicyInfoResponseDto.recommendPolicyInfo> result = new ArrayList<>();
        result.add(
                RecommendPolicyInfoResponseDto.recommendPolicyInfo.builder()
                        .supportTypeDescription(loansPolicy.getSupportPolicyType().getDescription())
                        .supportType(loansPolicy.getSupportPolicyType())
                        .policyId(loansPolicy.getId())
                        .policyName(loansPolicy.getPolicyName())
                        .policyLogo(loansPolicy.getPolicyLogo())
                        .policyAreaCode(loansPolicy.getAreaCode().getName())
                        .policyCityCode(loansPolicy.getCityCode().getName())
                        .policyIntroduction(loansPolicy.getPolicyIntroduction())
                        .supportTypePolicyCnt(policyRepository.getPolicyCntBySupportPolicyType(SupportPolicyType.LOANS))
                        .benefit(10000000)
                        .policyDateType("기간 신청")
                        .build()
        );
        result.add(
                RecommendPolicyInfoResponseDto.recommendPolicyInfo.builder()
                        .supportType(moneyPolicy.getSupportPolicyType())
                        .supportTypeDescription(moneyPolicy.getSupportPolicyType().getDescription())
                        .policyId(moneyPolicy.getId())
                        .policyName(moneyPolicy.getPolicyName())
                        .policyLogo(moneyPolicy.getPolicyLogo())
                        .policyAreaCode(moneyPolicy.getAreaCode().getName())
                        .policyCityCode(moneyPolicy.getCityCode().getName())
                        .policyIntroduction(moneyPolicy.getPolicyIntroduction())
                        .supportTypePolicyCnt(policyRepository.getPolicyCntBySupportPolicyType(SupportPolicyType.MONEY))
                        .benefit(10000000)
                        .policyDateType("상시")
                        .build()
        );
        result.add(
                RecommendPolicyInfoResponseDto.recommendPolicyInfo.builder()
                        .supportType(socialServicePolicy.getSupportPolicyType())
                        .supportTypeDescription(socialServicePolicy.getSupportPolicyType().getDescription())
                        .policyId(socialServicePolicy.getId())
                        .policyName(socialServicePolicy.getPolicyName())
                        .policyLogo(socialServicePolicy.getPolicyLogo())
                        .policyAreaCode(socialServicePolicy.getAreaCode().getName())
                        .policyCityCode(socialServicePolicy.getCityCode().getName())
                        .policyIntroduction(socialServicePolicy.getPolicyIntroduction())
                        .supportTypePolicyCnt(policyRepository.getPolicyCntBySupportPolicyType(SupportPolicyType.SOCIAL_SERVICE))
                        .benefit(10000000)
                        .policyDateType("미정")
                        .build()
        );
        return RecommendPolicyInfoResponseDto.builder().policyInfos(result).build();
    }
}

