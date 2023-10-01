package com.cmc.zenefitserver.domain.user.application;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.domain.user.domain.Address;
import com.cmc.zenefitserver.domain.user.domain.Gender;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.user.domain.UserDetail;
import com.cmc.zenefitserver.domain.user.dto.*;
import com.cmc.zenefitserver.domain.userpolicy.dao.UserPolicyRepository;
import com.cmc.zenefitserver.global.auth.jwt.JwtService;
import com.cmc.zenefitserver.global.common.request.TokenRequestDto;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.cmc.zenefitserver.global.error.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final UserPolicyRepository userPolicyRepository;
    private final AmazonS3Client amazonS3Client;
    private final JwtService jwtService;

    // 회원가입
    @Transactional
    public TokenResponseDto signUp(SignUpRequestDto signUpRequestDto) {

        // 닉네임 중복 여부 확인
//        userRepository.findByNickname(signUpRequestDto.getNickname())
//                .ifPresent(user -> {
//                    throw new BusinessException(DUPLICATE_NICKNAME);
//                });

        // 이메일 + provider 중복 여부 확인
//        userRepository.findByEmailAndProvider(signUpRequestDto.getEmail(), signUpRequestDto.getProvider())
//                .ifPresent(user -> {
//                    throw new BusinessException(DUPLICATE_EMAIL_PROVIDER);
//                });

        User findUser = userRepository.findByEmailAndProvider(signUpRequestDto.getEmail(), signUpRequestDto.getProvider())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        findUser.updateUser(signUpRequestDto);
        return jwtService.createToken(new TokenRequestDto(findUser));
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

    public UserInfoResponseDto getUserInfo(User user) {
        return UserInfoResponseDto.builder()
                .nickname(user.getNickname())
                .age(user.getAge())
                .area(user.getAddress().getAreaCode().getName())
                .city(user.getAddress().getCityCode().getName())
                .lastYearIncome(user.getLastYearIncome())
                .educationType(user.getEducationType().getDescription())
                .jobs(user.getJobs().stream().map(jobType -> jobType.getDescription()).collect(Collectors.toSet()))
                .gender(user.getUserDetail().getGender().getDescription())
                .smallBusiness(user.getUserDetail().isSmallBusiness())
                .soldier(user.getUserDetail().isSoldier())
                .lowIncome(user.getUserDetail().isLowIncome())
                .disabled(user.getUserDetail().isDisabled())
                .localTalent(user.getUserDetail().isLocalTalent())
                .farmer(user.getUserDetail().isFarmer())
                .build();
    }

    public HomeInfoResponseDto getHomeInfo(User user) {

        // 신청 정책과 수혜 정책의 수
        int interestPolicyCount = userPolicyRepository.getInterestPolicyCount(user.getUserId());
        int applyPolicyCount = userPolicyRepository.getApplyPolicyCount(user.getUserId());
        int sumPolicyCount = interestPolicyCount + applyPolicyCount;
        Gender gender = user.getUserDetail().getGender();

        // 추천 정책 조회
        String characterImageUrl = getImageUrl(gender, sumPolicyCount);

        // 수혜 정도에 따른 유저 이미지 조회 및 알고리즘


        // 지원 정책 유형에 따른 신청 마감일에 임박한 정책 조회
        LocalDate currentTime = LocalDate.now();
        List<HomeInfoResponseDto.HomePolicyInfo> endDateHomePolicyInfoList = Arrays.stream(SupportPolicyType.values())
                .map(type -> {
                    Policy findPolicy = policyRepository.findMostImminentNonAppliedPolicy(user.getUserId(), type, currentTime, PageRequest.of(0, 1)).get(0);
                    HomeInfoResponseDto.HomePolicyInfo homePolicyInfo = HomeInfoResponseDto.HomePolicyInfo.builder()
                            .policyName(findPolicy.getPolicyName())
                            .supportPolicyType(findPolicy.getSupportPolicyType())
                            .supportPolicyTypeName(findPolicy.getSupportPolicyType().getDescription())
                            .endDate(findPolicy.getEndDate())
                            .build();
                    return homePolicyInfo;
                })
                .collect(Collectors.toList());


        return HomeInfoResponseDto.builder()
                .nickname(user.getNickname())
                .userImage(characterImageUrl)
                .benefit(user.getBenefit())
                .interestPolicyCnt(interestPolicyCount)
                .applyPolicyCnt(applyPolicyCount)
                .recommendPolicy(null)
                .endDatePolicy(endDateHomePolicyInfoList)
                .build();
    }

    public String getImageUrl(Gender gender, int sumPolicyCount) {

        String bucketName = "zenefit-bucket";
        String folderName = "character";

        String bucketImageUrl = gender.getCode() + "-";

        bucketImageUrl = getImageString(sumPolicyCount, bucketImageUrl);

        String s3ObjectKey = folderName + "/" + bucketImageUrl + ".png"; // 이미지 파일의 객체 키

        Date expiration = new Date(System.currentTimeMillis() + 3600000); // URL의 만료 시간 (1시간)
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, s3ObjectKey).withExpiration(expiration);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString(); // 이미지 URL 반환
    }

    private static String getImageString(int sumPolicyCount, String bucketImageUrl) {
        if (sumPolicyCount >= 0 && sumPolicyCount <= 3) {
            bucketImageUrl += "no";
        }
        if (sumPolicyCount >= 4 && sumPolicyCount <= 9) {
            bucketImageUrl += "new";
        }
        if (sumPolicyCount >= 10 && sumPolicyCount <= 12) {
            bucketImageUrl += "save";
        }
        if (sumPolicyCount >= 13) {
            bucketImageUrl += "smart";
        }
        return bucketImageUrl;
    }

    public void updateFcmToken(User user, String fcmToken) {
        user.updateFcmToken(fcmToken);
        userRepository.save(user);
    }

    public void updatePushNotificationStatus(User user, boolean pushNotificationStatus) {
        user.updatePushNotificationStatus(pushNotificationStatus);
        userRepository.save(user);
    }

}
