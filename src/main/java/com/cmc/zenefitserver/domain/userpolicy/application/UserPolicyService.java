package com.cmc.zenefitserver.domain.userpolicy.application;

import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.userpolicy.dao.UserPolicyRepository;
import com.cmc.zenefitserver.domain.userpolicy.domain.UserPolicy;
import com.cmc.zenefitserver.domain.userpolicy.dto.ApplyPolicyListResponseDto;
import com.cmc.zenefitserver.domain.userpolicy.dto.InterestPolicyListResponseDto;
import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserPolicyService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final UserPolicyRepository userPolicyRepository;

    @Transactional
    public void saveInterestPolicy(User user, Long policyId) {

        User findUser = userRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        Policy findPolicy = policyRepository.findById(policyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_POLICY));

        UserPolicy findUserPolicy = userPolicyRepository.findByUser_userIdAndPolicy_Id(findUser.getUserId(), findPolicy.getId())
                .orElse(null);

        if (findUserPolicy == null) {
             findUserPolicy = UserPolicy.builder()
                    .user(findUser)
                    .policy(findPolicy)
                    .interestFlag(true)
                    .build();
        }

        if(findUserPolicy != null){
            findUserPolicy.setInterestFlagToTrue();
        }

        userPolicyRepository.save(findUserPolicy);
    }

    public void saveApplyPolicy(User user, Long policyId) {
        User findUser = userRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        Policy findPolicy = policyRepository.findById(policyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_POLICY));

        UserPolicy findUserPolicy = userPolicyRepository.findByUser_userIdAndPolicy_Id(findUser.getUserId(), findPolicy.getId())
                .orElse(null);

        if (findUserPolicy == null) {
            findUserPolicy = UserPolicy.builder()
                    .user(findUser)
                    .policy(findPolicy)
                    .applyFlag(true)
                    .build();
        }

        if(findUserPolicy != null){
            findUserPolicy.setApplyFlagToTrue();
        }

        userPolicyRepository.save(findUserPolicy);
    }

    public List<InterestPolicyListResponseDto> getInterestPolicyList(User user) {
        return userPolicyRepository.findAllByUser_userIdAndInterestFlag(user.getUserId(), true)
                .stream()
                .map(entity -> {
                    Policy policy = entity.getPolicy();
                    InterestPolicyListResponseDto dto = InterestPolicyListResponseDto.builder()
                            .policyId(policy.getId())
                            .policyName(policy.getPolicyName())
                            .policyIntroduction(policy.getPolicyIntroduction())
                            .policyEndDate(LocalDate.of(2023,2,12))
                            .policyAgency("임시 기관")
                            .policyAgencyLogo("임시 기관 로고")
                            .build();
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ApplyPolicyListResponseDto> getApplyPolicyList(User user) {
        return userPolicyRepository.findAllByUser_userIdAndApplyFlag(user.getUserId(), true)
                .stream()
                .map(entity -> {
                    Policy policy = entity.getPolicy();
                    ApplyPolicyListResponseDto dto = ApplyPolicyListResponseDto.builder()
                            .policyId(policy.getId())
                            .policyName(policy.getPolicyName())
                            .policyIntroduction(policy.getPolicyIntroduction())
                            .policyBenefit(policy.getBenefit())
                            .policyAgency(policy.getAgency())
                            .policyAgencyLogo(policy.getAgencyLogo())
                            .build();
                    return dto;
                })
                .collect(Collectors.toList());

    }

    @Transactional
    public void deleteInterestPolicy(User user, Long policyId) {
        UserPolicy findUserPolicy = userPolicyRepository.findByUser_userIdAndPolicy_Id(user.getUserId(), policyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_POLICY));

        // 수혜 정책 X -> delete
        if(!findUserPolicy.isApplyFlag()){
            userPolicyRepository.delete(findUserPolicy);
        }

        // 수혜 정책 O -> 수정
        if(findUserPolicy.isApplyFlag()){
            findUserPolicy.setInterestFlagToFalse();
        }

    }

    @Transactional
    public void deleteApplyPolicy(User user, Long policyId) {
        UserPolicy findUserPolicy = userPolicyRepository.findByUser_userIdAndPolicy_Id(user.getUserId(), policyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_POLICY));

        // 관심 정책 X
        if(!findUserPolicy.isInterestFlag()){
            userPolicyRepository.delete(findUserPolicy);
        }

        // 관심 정책 O -> 수정
        if(findUserPolicy.isInterestFlag()){
            findUserPolicy.setApplyFlagToFalse();
        }

    }
}
