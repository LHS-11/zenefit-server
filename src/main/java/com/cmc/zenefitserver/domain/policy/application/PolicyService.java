package com.cmc.zenefitserver.domain.policy.application;

import com.cmc.zenefitserver.domain.policy.dao.PolicyQueryRepository;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyCode;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public Slice<PolicyListResponseDto> getPolicyList(User user, PolicyListRequestDto policyListRequestDto, Pageable pageable) {
        Slice<PolicyListResponseDto> policyListResponseDtos = policyQueryRepository.searchBySlice(
                user,
                policyListRequestDto.getLastPolicyId(),
                SupportPolicyType.fromString(policyListRequestDto.getSupportPolicyType()),
                PolicyCode.fromString(policyListRequestDto.getPolicyType()),
                pageable);
        policyListResponseDtos.stream()
                .forEach(policyListResponseDto -> {
                    policyListResponseDto.updateAreaCode(policyListResponseDto.getAreaCode());
                    policyListResponseDto.updateCityCode(policyListResponseDto.getCityCode());
                });

        return policyListResponseDtos;
    }

    public Slice<PolicyListResponseDto> getSearchPolicyList(User user, SearchPolicyListRequestDto searchPolicyDto, Pageable pageable) {
        Slice<PolicyListResponseDto> policyListResponseDtos = policyQueryRepository.searchBySlice(
                user,
                searchPolicyDto.getLastPolicyId(),
                SupportPolicyType.fromString(searchPolicyDto.getSupportPolicyType()),
                PolicyCode.fromString(searchPolicyDto.getPolicyType()),
                searchPolicyDto.getKeyword(),
                pageable);
        policyListResponseDtos.stream()
                .forEach(policyListResponseDto -> {
                    policyListResponseDto.updateAreaCode(policyListResponseDto.getAreaCode());
                    policyListResponseDto.updateCityCode(policyListResponseDto.getCityCode());
                });
        return policyListResponseDtos;
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
                .applicationSite(policy.getApplicationSiteAddress())
                .referenceSite(policy.getReferenceSiteUrlAddress())
                .build();

        return dto;
    }

    public List<CalendarPolicyListResponseDto> getPolicyListBySearchDate(User user, LocalDate searchDate, String type) {

        LocalDate searchSttDate = searchDate.withDayOfMonth(1);
        LocalDate searchEndDate = searchDate.withDayOfMonth(searchDate.lengthOfMonth());

        List<CalendarPolicyListResponseDto> result = null;
        if (SearchDateType.STT_DATE.name().equals(type)) {

            List<UserPolicy> userPolices = userPolicyRepository.findAllByUser_userIdAndInterestFlagAndPolicy_ApplySttDateBetween(
                    user.getUserId(), true, searchSttDate, searchEndDate
            );

            if (userPolices != null) {
                result = userPolices.stream()
                        .map(entity -> {
                            Policy policy = entity.getPolicy();
                            CalendarPolicyListResponseDto dto = CalendarPolicyListResponseDto.builder()
                                    .policyId(policy.getId())
                                    .policyName(policy.getPolicyName())
                                    .policyApplyStatus(policy.getApplyStatus())
                                    .policyAgencyLogo(policy.getPolicyLogo())
                                    .policySttDateOrEndDate(policy.getApplySttDate())
                                    .build();
                            return dto;
                        })
                        .collect(Collectors.toList());
            }

        }

        if (SearchDateType.END_DATE.name().equals(type)) {

            List<UserPolicy> userPolices = userPolicyRepository.findAllByUser_userIdAndInterestFlagAndPolicy_ApplyEndDateBetween(
                    user.getUserId(), true, searchSttDate, searchEndDate
            );
            if (userPolices != null) {
                result = userPolices.stream()
                        .map(entity -> {
                            Policy policy = entity.getPolicy();
                            CalendarPolicyListResponseDto dto = CalendarPolicyListResponseDto.builder()
                                    .policyId(policy.getId())
                                    .policyName(policy.getPolicyName())
                                    .policyApplyStatus(policy.getApplyStatus())
                                    .policyAgencyLogo(policy.getPolicyLogo())
                                    .policySttDateOrEndDate(policy.getApplyEndDate())
                                    .build();
                            return dto;
                        })
                        .collect(Collectors.toList());
            }
        }

        return result;
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
}

