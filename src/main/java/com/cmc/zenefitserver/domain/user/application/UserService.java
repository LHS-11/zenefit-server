package com.cmc.zenefitserver.domain.user.application;

import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.user.domain.UserDetail;
import com.cmc.zenefitserver.domain.user.dto.ModifyRequestDto;
import com.cmc.zenefitserver.domain.user.dto.SignUpRequestDto;
import com.cmc.zenefitserver.domain.user.dto.SocialInfoResponseDto;
import com.cmc.zenefitserver.global.auth.jwt.JwtService;
import com.cmc.zenefitserver.global.common.request.TokenRequestDto;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.cmc.zenefitserver.global.error.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    // 회원가입
    public TokenResponseDto signUp(SignUpRequestDto signUpRequestDto) {

        // 닉네임 중복 여부 확인
        userRepository.findByNickname(signUpRequestDto.getNickname())
                .ifPresent(user -> {
                    throw new BusinessException(DUPLICATE_NICKNAME);
                });

        // 이메일 + provider 중복 여부 확인
        userRepository.findByEmailAndProvider(signUpRequestDto.getEmail(), signUpRequestDto.getProvider())
                .ifPresent(user -> {
                    throw new BusinessException(DUPLICATE_EMAIL_PROVIDER);
                });

        User user = User.builder()
                .email(signUpRequestDto.getEmail())
                .nickname(signUpRequestDto.getNickname())
                .age(signUpRequestDto.getAge())
                .lastYearIncome(signUpRequestDto.getLastYearIncome())
                .address(signUpRequestDto.getAddress())
                .educationType(signUpRequestDto.getEducationType())
                .jobs(signUpRequestDto.getJobs())
                .provider(signUpRequestDto.getProvider())
                .build();

        UserDetail userDetail = UserDetail.builder()
                .gender(signUpRequestDto.getGender())
                .build();

        user.setUserDetail(userDetail);
        userDetail.setUser(user);

        User savedUser = userRepository.save(user);
        return jwtService.createToken(new TokenRequestDto(savedUser));
    }

    // 회원정보 수정
    @Transactional
    public User modify(ModifyRequestDto modifyRequestDto, User user) {

        System.out.println("modifyRequestDto = " + modifyRequestDto);
        User findUser = userRepository.findByUserId(user.getUserId()).get();

        if (!findUser.getNickname().equals(modifyRequestDto.getNickname())) {
            // 닉네임 중복 여부
            userRepository.findByNickname(modifyRequestDto.getNickname())
                    .ifPresent(u -> {
                        throw new BusinessException(DUPLICATE_NICKNAME);
                    });
        }
        findUser.update(modifyRequestDto);
        return findUser;
    }

    // 관심 정책 및 정책 추가하기
//    @Transactional
//    public void addPolicy(Long userId, Long policyId,UserPolicyType userPolicyType){
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));
//
//        Policy policy = policyRepository.findById(policyId)
//                .orElseThrow(() -> new BusinessException(NOT_FOUND_POLICY));
//
//        UserPolicy userPolicy = UserPolicy.builder()
//                .user(user)
//                .policy(policy)
//                .userPolicyType(userPolicyType)
//                .build();
//
//        userPolicyRepository.save(userPolicy);
//    }

    // AreaCode 가져오기
    public List<String> getAreaCodes() {
        return Arrays.stream(AreaCode.values()).map(areaCode -> areaCode.getName()).collect(Collectors.toList());
    }

    // CityCode 가져오기
    public List<String> getCityCodes(String areaCode) {
        return AreaCode.findCityCodes(areaCode);
    }

    public SocialInfoResponseDto getSocialInfo(User user) {
        return SocialInfoResponseDto.builder()
                .email(user.getEmail())
                .provider(user.getProvider())
                .build();
    }
}
