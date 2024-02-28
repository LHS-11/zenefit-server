package com.cmc.zenefitserver.domain.policy.application;

import com.cmc.zenefitserver.domain.policy.dao.PolicyQueryRepository;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.*;
import com.cmc.zenefitserver.domain.policy.dto.request.PolicyListRequestDto;
import com.cmc.zenefitserver.domain.policy.dto.request.SearchPolicyListRequestDto;
import com.cmc.zenefitserver.domain.policy.dto.response.*;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.user.dto.response.HomeInfoResponseDto;
import com.cmc.zenefitserver.domain.userpolicy.application.ImageClassifier;
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
    private final ImageClassifier imageClassifier;

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
                    dto.updatePolicyLogo(imageClassifier.getLogo(findPolicy));
                    dto.updateBenefitPeriod(CashBenefitType.findCashBenefit(findPolicy));
//                    dto.updatePolicyUrl(dto.getPolicyUrl().startsWith("http"));
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
                        dto.updatePolicyLogo(imageClassifier.getLogo(findPolicy));
                        dto.updateBenefitPeriod(CashBenefitType.findCashBenefit(findPolicy));
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
                .policyIntroduction(removeNullOrHyphenStr(policy.getPolicyIntroduction()))
                .policyApplyDenialReason(denialReasonType != null ? denialReasonType.getText() : null)
                .policyApplyDocument(removeNullOrHyphenStr(policy.getSubmissionDocumentContent()))
                .policyApplyMethod(removeNullOrHyphenStr(policy.getApplicationProcedureContent()))
                .policyApplyDate(policy.getApplicationPeriodContent())
                .policyDateType(policy.getPolicyDateType())
                .policyDateTypeDescription(policy.getPolicyDateType().getDescription())
                .applicationSite(policy.getApplicationSiteAddress())
                .referenceSite(policy.getReferenceSiteUrlAddress())
                .benefit(policy.getBenefit())
                .benefitPeriod(CashBenefitType.findCashBenefit(policy))
                .applyFlag(userPolicy == null ? false : userPolicy.isApplyFlag())
                .interestFlag(userPolicy == null ? false : userPolicy.isInterestFlag())
                .policyMethodType(findPolicyMethodType)
                .policyMethodTypeDescription(findPolicyMethodType.getDescription())
                .build();

        return dto;
    }

    public String removeNullOrHyphenStr(String str){
        if(str.trim().equals("-") || str.toLowerCase().trim().contains("null")){
            return "";
        }
        return str;
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
                                .applyProcedure(PolicyMethodType.findPolicyMethodTypeByKeywords(policy.getApplicationProcedureContent()))
                                .policyAgencyLogo(imageClassifier.getLogo(policy))
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
                            .applyProcedure(PolicyMethodType.findPolicyMethodTypeByKeywords(policy.getApplicationProcedureContent()))
                            .policyAgencyLogo(imageClassifier.getLogo(policy))
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
                            .policyLogo(imageClassifier.getLogo(policy))
                            .policyAreaCode(policy.getAreaCode().getName())
                            .policyCityCode(policy.getCityCode() != null ? policy.getCityCode().getName() : null)
                            .supportType(supportPolicyType)
                            .supportTypeDescription(supportPolicyType.getDescription())
                            .policyIntroduction(policy.getPolicyIntroduction())
                            .supportTypePolicyCnt(policyRepository.getPolicyCntBySupportPolicyType(supportPolicyType))
                            .benefit(10000000)
                            .applyFlag(userPolicy == null ? false : userPolicy.isApplyFlag())
                            .interestFlag(userPolicy == null ? false : userPolicy.isInterestFlag())
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
                            .policyLogo(imageClassifier.getLogo(policy))
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

