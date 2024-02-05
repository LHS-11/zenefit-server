package com.cmc.zenefitserver.domain.policy.application;

import com.cmc.zenefitserver.domain.policy.dao.PolicyQueryRepository;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.DenialReasonType;
import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyMethodType;
import com.cmc.zenefitserver.domain.policy.domain.enums.SearchDateType;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import com.cmc.zenefitserver.domain.policy.dto.*;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.user.dto.HomeInfoResponseDto;
import com.cmc.zenefitserver.domain.userpolicy.dao.UserPolicyRepository;
import com.cmc.zenefitserver.domain.userpolicy.domain.UserPolicy;
import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
                    DenialReasonType denialReasonType = PolicyDenialReasonClassifier.getDenialReasonType(user, findPolicy);
                    dto.updatePolicyApplyDenialReason(denialReasonType != null ? denialReasonType.getText() : null);
                    dto.updatePolicyDateTypeDescription(dto.getPolicyDateType());
                    dto.updateAreaCode(dto.getAreaCode());
                    dto.updateCityCode(dto.getCityCode());
                    dto.updatePolicyMethodType(dto.getPolicyMethodTypeDescription());
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
                    dto.updatePolicyMethodType(dto.getPolicyMethodTypeDescription());
                    return dto;
                });
    }

    public PolicyInfoResponseDto getPolicy(User user, Long policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_POLICY));

        UserPolicy userPolicy = userPolicyRepository.findByUser_userIdAndPolicy_Id(user.getUserId(), policyId)
                .orElseGet(() -> null);

        // 신청 불가 사유 로직
        PolicyMethodType findPolicyMethodType = PolicyMethodType.findPolicyMethodTypeByKeywords(policy.getApplicationProcedureContent());
        DenialReasonType denialReasonType = PolicyDenialReasonClassifier.getDenialReasonType(user, policy);

        PolicyInfoResponseDto dto = PolicyInfoResponseDto.builder()
                .policyId(policy.getId())
                .policyName(policy.getPolicyName())
                .policyIntroduction(policy.getPolicyIntroduction())
                .policyApplyDenialReason(denialReasonType != null ? denialReasonType.getText() : null)
                .policyApplyDocument(policy.getSubmissionDocumentContent())
                .policyApplyMethod(policy.getApplicationProcedureContent())
                .policyApplyDate(policy.getApplicationPeriodContent())
                .policyDateType(policy.getPolicyDateType())
                .policyDateTypeDescription(policy.getPolicyDateType().getDescription())
                .applicationSite(policy.getApplicationSiteAddress())
                .referenceSite(policy.getReferenceSiteUrlAddress())
                .benefit(policy.getBenefit())
                .applyFlag(userPolicy == null ? false : userPolicy.isApplyFlag())
                .interestFlag(userPolicy == null ? false : userPolicy.isInterestFlag())
                .policyMethodType(findPolicyMethodType)
                .policyMethodTypeDescription(findPolicyMethodType.getDescription())
                .build();

        return dto;
    }

    public List<CalendarPolicyListResponseDto> getPolicyListBySearchMonth(User user, LocalDate searchDate) {

        LocalDate searchSttDate = searchDate.withDayOfMonth(1);
        LocalDate searchEndDate = searchDate.withDayOfMonth(searchDate.lengthOfMonth());

        List<CalendarPolicyListResponseDto> result = null;

        List<Policy> polices = policyRepository.findAllBySearchMonth(user.getUserId(), searchSttDate, searchEndDate);

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

    public List<CalendarPolicyListResponseDto> getPolicyListBySearchDate(User user, LocalDate searchDate, SearchDateType searchDateType) {

        List<Policy> policies = null;
        if (SearchDateType.STT_DATE.equals(searchDateType)) {
            policies = policyRepository.findAllBySearchSttDate(user.getUserId(), searchDate);
        }

        if (SearchDateType.END_DATE.equals(searchDateType)) {
            policies = policyRepository.findAllBySearchEndDate(user.getUserId(), searchDate);
        }

        return policies.stream()
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

    public RecommendPolicyInfoResponseDto recommend(User user) {

        Map<SupportPolicyType, Policy> supportPolicyTypePolicyMap = policyRecommender.recommendPolicy(user);

        List<RecommendPolicyInfoResponseDto.recommendPolicyInfo> recommendPolicyInfos = supportPolicyTypePolicyMap.keySet()
                .stream()
                .map(supportPolicyType -> {
                    Policy policy = supportPolicyTypePolicyMap.get(supportPolicyType);
                    PolicyMethodType findPolicyMethodType = PolicyMethodType.findPolicyMethodTypeByKeywords(policy.getApplicationProcedureContent());
                    UserPolicy userPolicy = userPolicyRepository.findByUser_userIdAndPolicy_Id(user.getUserId(), policy.getId())
                            .orElseGet(null);

                    RecommendPolicyInfoResponseDto.recommendPolicyInfo dto = RecommendPolicyInfoResponseDto.recommendPolicyInfo.builder()
                            .policyId(policy.getId())
                            .policyName(policy.getPolicyName())
                            .policyLogo(policy.getPolicyLogo())
                            .policyAreaCode(policy.getAreaCode().getName())
                            .policyCityCode(policy.getCityCode().getName())
                            .policyIntroduction(policy.getPolicyIntroduction())
                            .supportTypePolicyCnt(policyRepository.getPolicyCntBySupportPolicyType(supportPolicyType))
                            .benefit(10000000)
                            .applyFlag(userPolicy.isApplyFlag())
                            .interestFlag(userPolicy.isInterestFlag())
                            .policyDateType(policy.getPolicyDateType().getDescription())
                            .policyMethodType(findPolicyMethodType)
                            .policyMethodTypeDescription(findPolicyMethodType.getDescription())
                            .build();
                    return dto;
                }).collect(Collectors.toList());

        return RecommendPolicyInfoResponseDto.builder().policyInfos(recommendPolicyInfos).build();
    }

    public List<HomeInfoResponseDto.HomePolicyInfo> recommendPolicy(User user) {
        Map<SupportPolicyType, Policy> supportPolicyTypePolicyMap = policyRecommender.recommendPolicy(user);

        return Arrays.stream(SupportPolicyType.values())
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
        Policy moneyPolicy = policyRepository.findAllBySupportPolicyTypesContains(SupportPolicyType.MONEY).get(0);
        Policy loansPolicy = policyRepository.findAllBySupportPolicyTypesContains(SupportPolicyType.LOANS).get(0);
        Policy socialServicePolicy = policyRepository.findAllBySupportPolicyTypesContains(SupportPolicyType.SOCIAL_SERVICE).get(0);

        PolicyMethodType loansPolicyMethodType = PolicyMethodType.findPolicyMethodTypeByKeywords(loansPolicy.getApplicationProcedureContent());
        PolicyMethodType moneyPolicyMethodType = PolicyMethodType.findPolicyMethodTypeByKeywords(moneyPolicy.getApplicationProcedureContent());
        PolicyMethodType socialServicePolicyMethodType = PolicyMethodType.findPolicyMethodTypeByKeywords(socialServicePolicy.getApplicationProcedureContent());

        List<RecommendPolicyInfoResponseDto.recommendPolicyInfo> result = new ArrayList<>();
        result.add(
                RecommendPolicyInfoResponseDto.recommendPolicyInfo.builder()
                        .supportType(SupportPolicyType.MONEY)
                        .supportTypeDescription(SupportPolicyType.MONEY.getDescription())
                        .policyId(moneyPolicy.getId())
                        .policyName(moneyPolicy.getPolicyName())
                        .policyLogo(moneyPolicy.getPolicyLogo())
                        .policyAreaCode(moneyPolicy.getAreaCode().getName())
                        .policyCityCode(moneyPolicy.getAreaCode().getCities().size() != 0 ? moneyPolicy.getCityCode().getName() : null)
                        .policyIntroduction(moneyPolicy.getPolicyIntroduction())
                        .supportTypePolicyCnt(policyRepository.getPolicyCntBySupportPolicyType(SupportPolicyType.MONEY))
                        .benefit(10000000)
                        .applyFlag(true)
                        .interestFlag(false)
                        .policyDateType("상시")
                        .policyMethodType(moneyPolicyMethodType)
                        .policyMethodTypeDescription(moneyPolicyMethodType.getDescription())
                        .build()
        );
        result.add(
                RecommendPolicyInfoResponseDto.recommendPolicyInfo.builder()
                        .supportTypeDescription(SupportPolicyType.LOANS.getDescription())
                        .supportType(SupportPolicyType.LOANS)
                        .policyId(loansPolicy.getId())
                        .policyName(loansPolicy.getPolicyName())
                        .policyLogo(loansPolicy.getPolicyLogo())
                        .policyAreaCode(loansPolicy.getAreaCode().getName())
                        .policyCityCode(loansPolicy.getAreaCode().getCities().size() != 0 ? loansPolicy.getCityCode().getName() : null)
                        .policyIntroduction(loansPolicy.getPolicyIntroduction())
                        .supportTypePolicyCnt(policyRepository.getPolicyCntBySupportPolicyType(SupportPolicyType.LOANS))
                        .benefit(10000000)
                        .applyFlag(false)
                        .interestFlag(false)
                        .policyDateType("기간 신청")
                        .policyMethodType(loansPolicyMethodType)
                        .policyMethodTypeDescription(loansPolicyMethodType.getDescription())
                        .build()
        );
        result.add(
                RecommendPolicyInfoResponseDto.recommendPolicyInfo.builder()
                        .supportType(SupportPolicyType.SOCIAL_SERVICE)
                        .supportTypeDescription(SupportPolicyType.SOCIAL_SERVICE.getDescription())
                        .policyId(socialServicePolicy.getId())
                        .policyName(socialServicePolicy.getPolicyName())
                        .policyLogo(socialServicePolicy.getPolicyLogo())
                        .policyAreaCode(socialServicePolicy.getAreaCode().getName())
                        .policyCityCode(socialServicePolicy.getAreaCode().getCities().size() != 0 ? socialServicePolicy.getCityCode().getName() : null)
                        .policyIntroduction(socialServicePolicy.getPolicyIntroduction())
                        .supportTypePolicyCnt(policyRepository.getPolicyCntBySupportPolicyType(SupportPolicyType.SOCIAL_SERVICE))
                        .benefit(10000000)
                        .applyFlag(true)
                        .interestFlag(true)
                        .policyDateType("미정")
                        .policyMethodType(socialServicePolicyMethodType)
                        .policyMethodTypeDescription(socialServicePolicyMethodType.getDescription())
                        .build()
        );
        return RecommendPolicyInfoResponseDto.builder().policyInfos(result).build();
    }
}

