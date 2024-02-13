package com.cmc.zenefitserver.domain.policy.application;

import com.cmc.zenefitserver.domain.policy.dao.PolicyQueryRepository;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.DenialReasonType;
import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyMethodType;
import com.cmc.zenefitserver.domain.policy.domain.enums.SearchDateType;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import com.cmc.zenefitserver.domain.policy.dto.request.PolicyListRequestDto;
import com.cmc.zenefitserver.domain.policy.dto.request.SearchPolicyListRequestDto;
import com.cmc.zenefitserver.domain.policy.dto.response.*;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.user.dto.response.HomeInfoResponseDto;
import com.cmc.zenefitserver.domain.userpolicy.dao.UserPolicyRepository;
import com.cmc.zenefitserver.domain.userpolicy.domain.UserPolicy;
import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PolicyService {


    private final RedisTemplate redisTemplate;
    private final PolicyQueryRepository policyQueryRepository;
    private final PolicyRepository policyRepository;
    private final UserPolicyRepository userPolicyRepository;
    private final PolicyRecommender policyRecommender;
    private final PolicyImageClassifier policyImageClassifier;

    // 정책 리스트 조회 비즈니스 로직
    public Page<PolicyListInfoDto> getPolicyList(User user, PolicyListRequestDto policyListRequestDto, int page, int size, Sort sort) {
        PageRequest pageable = PageRequest.of(page, size, sort);

        return policyQueryRepository.searchByAppliedPaging(user, policyListRequestDto.getSupportPolicyType(), policyListRequestDto.getPolicyType(), null, pageable)
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

    public PolicyListResponseDto getSearchPolicyList(User user, SearchPolicyListRequestDto searchPolicyDto, int page, int size, Sort sort) {
        PageRequest appliedPageable = PageRequest.of(page, size, sort);

        // 유저가 신청 가능한 정책 가져오기
        Page<PolicyListInfoDto> appliedPolicyListInfo = policyQueryRepository.searchByAppliedPaging(user, searchPolicyDto.getSupportPolicyType(), searchPolicyDto.getPolicyType(), searchPolicyDto.getKeyword(), appliedPageable)
                .map(dto -> {
                    Policy findPolicy = policyRepository.findById(dto.getPolicyId()).get();
                    DenialReasonType denialReasonType = PolicyDenialReasonClassifier.getDenialReasonType(user, findPolicy);
                    dto.updatePolicyApplyDenialReason(denialReasonType != null ? denialReasonType.getText() : null);
                    dto.updatePolicyDateTypeDescription(dto.getPolicyDateType());
                    dto.updateAreaCode(dto.getAreaCode());
                    dto.updateCityCode(dto.getCityCode());
                    dto.updatePolicyMethodType(dto.getPolicyMethodTypeDescription());
                    dto.updatePolicyLogo(policyImageClassifier.getLogo(findPolicy));
                    return dto;
                });

        // 신청할 수 있는 정책 총 페이지
        int totalPages = appliedPolicyListInfo.getTotalPages();

        // 신청할 수 있는 정책 리스트의 마지막 페이지 일때
        if (totalPages - 1 < page) {

            List<Long> savedPolicyIds = getPolicyIdsByRedis(user);

            //신청 불가능한 정책 가져오기
            PageRequest nonAppliedPageable = PageRequest.of(page - totalPages, size, sort);

            Page<PolicyListInfoDto> nonAppliedPolicyListInfo = policyQueryRepository.searchByNonAppliedPaging(user, searchPolicyDto.getSupportPolicyType(), searchPolicyDto.getPolicyType(), searchPolicyDto.getKeyword(), nonAppliedPageable, savedPolicyIds)
                    .map(dto -> {
                        Policy findPolicy = policyRepository.findById(dto.getPolicyId()).get();
                        DenialReasonType denialReasonType = PolicyDenialReasonClassifier.getDenialReasonType(user, findPolicy);
                        dto.updatePolicyApplyDenialReason(denialReasonType != null ? denialReasonType.getText() : null);
                        dto.updatePolicyDateTypeDescription(dto.getPolicyDateType());
                        dto.updateAreaCode(dto.getAreaCode());
                        dto.updateCityCode(dto.getCityCode());
                        dto.updatePolicyMethodType(dto.getPolicyMethodTypeDescription());
                        dto.updatePolicyLogo(policyImageClassifier.getLogo(findPolicy));
                        return dto;
                    });

            return PolicyListResponseDto.builder()
                    .policyListInfoResponseDto(nonAppliedPolicyListInfo)
                    .pageNumber(page)
                    .last(nonAppliedPolicyListInfo.isLast())
                    .build();
        } else {
            savePolicyIdsToRedis(user, appliedPolicyListInfo);
        }

        return PolicyListResponseDto.builder()
                .policyListInfoResponseDto(appliedPolicyListInfo)
                .pageNumber(page)
                .last(false)
                .build();

    }

    private void savePolicyIdsToRedis(User user, Page<PolicyListInfoDto> policyListResponseDtos) {
        List<Long> policyIds = policyListResponseDtos.stream()
                .map(PolicyListInfoDto::getPolicyId)
                .collect(Collectors.toList());

        redisTemplate.opsForSet().add(String.valueOf(user.getUserId()), policyIds.toArray(new Long[0]));
//        redisTemplate.expire(user.getUserId(), 24, TimeUnit.HOURS);
    }

    private List<Long> getPolicyIdsByRedis(User user) {
        Set<Long> policyIds = redisTemplate.opsForSet().members(user.getUserId().toString());
        return policyIds.stream().collect(Collectors.toList());
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
                                .policyAgencyLogo(policyImageClassifier.getLogo(policy))
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
                            .policyAgencyLogo(policyImageClassifier.getLogo(policy))
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

        List<RecommendPolicyInfoResponseDto.recommendPolicyInfo> recommendPolicyInfos = Arrays.stream(SupportPolicyType.values())
                .map(supportPolicyType -> {
                    Policy policy = supportPolicyTypePolicyMap.get(supportPolicyType);
                    PolicyMethodType findPolicyMethodType = PolicyMethodType.findPolicyMethodTypeByKeywords(policy.getApplicationProcedureContent());
                    UserPolicy userPolicy = userPolicyRepository.findByUser_userIdAndPolicy_Id(user.getUserId(), policy.getId())
                            .orElse(null);

                    RecommendPolicyInfoResponseDto.recommendPolicyInfo dto = RecommendPolicyInfoResponseDto.recommendPolicyInfo.builder()
                            .policyId(policy.getId())
                            .policyName(policy.getPolicyName())
                            .policyLogo(policyImageClassifier.getLogo(policy))
                            .policyAreaCode(policy.getAreaCode().getName())
                            .policyCityCode(policy.getAreaCode().getCities().size() != 0 ? policy.getCityCode().getName() : null)
                            .supportType(supportPolicyType)
                            .supportTypeDescription(supportPolicyType.getDescription())
                            .policyIntroduction(policy.getPolicyIntroduction())
                            .supportTypePolicyCnt(policyRepository.getPolicyCntBySupportPolicyType(supportPolicyType))
                            .benefit(10000000)
                            .applyFlag(userPolicy == null ? false : userPolicy.isApplyFlag())
                            .interestFlag(userPolicy == null ? false  : userPolicy.isInterestFlag())
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
                            .policyLogo(policyImageClassifier.getLogo(policy))
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

