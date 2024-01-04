package com.cmc.zenefitserver.domain.userpolicy.application;

import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.userpolicy.dao.UserPolicyRepository;
import com.cmc.zenefitserver.domain.userpolicy.domain.UserPolicy;
import com.cmc.zenefitserver.domain.userpolicy.dto.ApplyPolicyListResponseDto;
import com.cmc.zenefitserver.domain.userpolicy.dto.InterestPolicyListResponseDto;
import com.cmc.zenefitserver.domain.userpolicy.dto.PolicySizeResponseDto;
import com.cmc.zenefitserver.global.error.ErrorCode;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserPolicyService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final UserPolicyRepository userPolicyRepository;

    public Page<InterestPolicyListResponseDto> getUserPoliciesByInterestFlag(User user, boolean interestFlag, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return userPolicyRepository.findUserPoliciesByUserAndInterestFlag(user, interestFlag, pageable)
                .map(userPolicy -> InterestPolicyListResponseDto.of(userPolicy.getPolicy()));
    }

    public Page<ApplyPolicyListResponseDto> getUserPoliciesByApplyFlag(User user, boolean applyFlag, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return userPolicyRepository.findUserPoliciesByUserAndApplyFlag(user, applyFlag, pageable)
                .map(userPolicy -> ApplyPolicyListResponseDto.of(userPolicy.getPolicy()));
    }

    @Transactional
    public void saveInterestPolicy(User user, Long policyId) {

        User findUser = userRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        Policy findPolicy = policyRepository.findById(policyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_POLICY));

        UserPolicy findUserPolicy = userPolicyRepository.findByUser_userIdAndPolicy_Id(findUser.getUserId(), findPolicy.getId())
                .orElse(null);

        if (findUserPolicy != null && findUserPolicy.isInterestFlag()) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_INTEREST_USER_POLICY);
        }

        if (findUserPolicy == null) {
            findUserPolicy = UserPolicy.builder()
                    .user(findUser)
                    .policy(findPolicy)
                    .interestFlag(true)
                    .build();
        }

        if (!findUserPolicy.isInterestFlag()) {
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

        if (findUserPolicy != null && findUserPolicy.isApplyFlag()) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_INTEREST_USER_POLICY);
        }

        if (findUserPolicy == null) {
            findUserPolicy = UserPolicy.builder()
                    .user(findUser)
                    .policy(findPolicy)
                    .applyFlag(true)
                    .build();
        }

        if (!findUserPolicy.isApplyFlag()) {
            findUserPolicy.setApplyFlagToTrue();
        }

        userPolicyRepository.save(findUserPolicy);
    }

    @Transactional
    public void deleteInterestPolicy(User user, Long policyId) {
        UserPolicy findUserPolicy = userPolicyRepository.findByUser_userIdAndPolicy_Id(user.getUserId(), policyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_POLICY));

        // 수혜 정책 X -> delete
        if (!findUserPolicy.isApplyFlag()) {
            userPolicyRepository.delete(findUserPolicy);
        }

        // 수혜 정책 O -> 수정
        if (findUserPolicy.isApplyFlag()) {
            findUserPolicy.setInterestFlagToFalse();
        }

    }

    @Transactional
    public void deleteApplyPolicy(User user, Long policyId) {
        UserPolicy findUserPolicy = userPolicyRepository.findByUser_userIdAndPolicy_Id(user.getUserId(), policyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_POLICY));

        // 관심 정책 X
        if (!findUserPolicy.isInterestFlag()) {
            userPolicyRepository.delete(findUserPolicy);
        }

        // 관심 정책 O -> 수정
        if (findUserPolicy.isInterestFlag()) {
            findUserPolicy.setApplyFlagToFalse();
        }

    }

    @Transactional
    public void deleteAllInterestPolicy(User user) {
        userPolicyRepository.deleteAllByUserAndInterestFlag(user, true);
    }

    @Transactional
    public void deleteAllApplyPolicy(User user) {
        userPolicyRepository.deleteAllByUserAndApplyFlag(user, true);
    }

    public PolicySizeResponseDto getAllInterestPolicySize(User user) {
        return PolicySizeResponseDto.builder()
                .size(userPolicyRepository.getInterestPolicyCount(user.getUserId()))
                .build();
    }

    public PolicySizeResponseDto getAllApplyPolicySize(User user) {
        return PolicySizeResponseDto.builder()
                .size(userPolicyRepository.getApplyPolicyCount(user.getUserId()))
                .build();
    }


//    public Slice<InterestAndApplyPolicyListResponseDto> getInterestPolicyListByPaging(User user, Long lastPolicyId, Pageable pageable) {
//        return userPolicyQueryRepository.searchInterestPolicyBySlice(user, lastPolicyId, pageable);
//    }
//
//    public Slice<ApplyPolicyListResponseDto> getApplyPolicyListByPaging(User user, Long lastPolicyId, Pageable pageable) {
//        return userPolicyQueryRepository.searchApplyPolicyBySlice(user, lastPolicyId, pageable);
//    }
//
//    public List<InterestAndApplyPolicyListResponseDto> getInterestPolicyList(User user) {
//        return userPolicyRepository.findAllByUser_userIdAndInterestFlag(user.getUserId(), true)
//                .stream()
//                .map(entity -> {
//                    Policy policy = entity.getPolicy();
//                    InterestAndApplyPolicyListResponseDto dto = InterestAndApplyPolicyListResponseDto.builder()
//                            .policyId(policy.getId())
//                            .policyName(policy.getPolicyName())
//                            .policyIntroduction(policy.getPolicyIntroduction())
//                            .policyLogo(policy.getPolicyLogo())
//                            .build();
//                    return dto;
//                })
//                .collect(Collectors.toList());
//    }
//
//    public List<ApplyPolicyListResponseDto> getApplyPolicyList(User user) {
//        return userPolicyRepository.findAllByUser_userIdAndApplyFlag(user.getUserId(), true)
//                .stream()
//                .map(entity -> {
//                    Policy policy = entity.getPolicy();
//                    ApplyPolicyListResponseDto dto = ApplyPolicyListResponseDto.builder()
//                            .policyId(policy.getId())
//                            .policyName(policy.getPolicyName())
//                            .policyIntroduction(policy.getPolicyIntroduction())
//                            .policyLogo(policy.getPolicyLogo())
//                            .build();
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//    }

}
