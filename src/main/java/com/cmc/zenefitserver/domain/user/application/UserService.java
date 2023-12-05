package com.cmc.zenefitserver.domain.user.application;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.cmc.zenefitserver.domain.policy.application.PolicyService;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.domain.user.domain.Character;
import com.cmc.zenefitserver.domain.user.domain.Gender;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.user.dto.*;
import com.cmc.zenefitserver.domain.userpolicy.dao.UserPolicyRepository;
import com.cmc.zenefitserver.domain.userpolicy.domain.UserPolicy;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    private final PolicyService policyService;

    // 회원가입
    @Transactional
    public TokenResponseDto signUp(SignUpRequestDto signUpRequestDto) {

        User findUser = userRepository.findByUserId(Long.parseLong(signUpRequestDto.getUserId()))
                .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));

        findUser.updateUser(signUpRequestDto);
        TokenResponseDto tokenResponseDto = jwtService.createToken(new TokenRequestDto(findUser));
        tokenResponseDto.updateNickname(findUser.getNickname());
        return tokenResponseDto;
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
        return Arrays.stream(AreaCode.values())
                .filter(areaCode -> !areaCode.getCode().equals("003002000"))
                .map(areaCode -> areaCode.getName())
                .collect(Collectors.toList());
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

        // 관심 정책과 수혜 정책의 합에 따른 캐릭터 산출
        Character findCharacter = Character.getCharacter(sumPolicyCount);

        // 캐릭터 이미지
        String characterImageUrl = getCharacterImageUrl(gender, findCharacter);

        // 지원 정책 유형에 따른 추천 정책 조회 - recommendPolicy
//        List<HomeInfoResponseDto.HomePolicyInfo> recommendPolicyInfoList = policyService.recommendPolicy(user);
        List<HomeInfoResponseDto.HomePolicyInfo> recommendPolicyInfoList = getRecommendPolicyDummy();

        // 지원 정책 유형에 따른 신청 마감일에 임박한 정책 조회
        LocalDate currentTime = LocalDate.now();
        List<HomeInfoResponseDto.HomePolicyInfo> endDateHomePolicyInfoList = Arrays.stream(SupportPolicyType.values())
                .map(type -> {
                    Policy findPolicy = policyRepository.findMostImminentNonAppliedPolicy(user.getUserId(), type, currentTime, PageRequest.of(0, 1)).get(0);
                    HomeInfoResponseDto.HomePolicyInfo homePolicyInfo = HomeInfoResponseDto.HomePolicyInfo.builder()
                            .policyId(findPolicy.getId())
                            .policyName(findPolicy.getPolicyName())
                            .policyLogo(findPolicy.getPolicyLogo())
                            .supportPolicyType(findPolicy.getSupportPolicyType())
                            .supportPolicyTypeName(findPolicy.getSupportPolicyType().getDescription())
                            .dueDate(ChronoUnit.DAYS.between(currentTime, findPolicy.getApplyEndDate()))
                            .build();
                    return homePolicyInfo;
                })
                .collect(Collectors.toList());


        return HomeInfoResponseDto.builder()
                .nickname(user.getNickname())
                .characterImage(characterImageUrl)
                .characterNickname(findCharacter.getName())
                .characterPercent(findCharacter.getPercent())
                .description(findCharacter.getDescription())
                .interestPolicyCnt(interestPolicyCount)
                .applyPolicyCnt(applyPolicyCount)
                .recommendPolicy(recommendPolicyInfoList)
                .endDatePolicy(endDateHomePolicyInfoList)
                .build();
    }

    public String getCharacterImageUrl(Gender gender, Character character) {

        String bucketName = "zenefit-bucket";
        String folderName = "character";

        String bucketImageUrl = gender.getCode() + "-" + character.name().toLowerCase();

        String s3ObjectKey = folderName + "/" + bucketImageUrl + ".png"; // 이미지 파일의 객체 키

        Date expiration = new Date(System.currentTimeMillis() + 3600000); // URL의 만료 시간 (1시간)
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, s3ObjectKey).withExpiration(expiration);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString(); // 이미지 URL 반환
    }

    public void updateFcmToken(User user, String fcmToken) {
        user.updateFcmToken(fcmToken);
        userRepository.save(user);
    }

    public void updatePushNotificationStatus(User user, boolean pushNotificationStatus) {
        user.updatePushNotificationStatus(pushNotificationStatus);
        userRepository.save(user);
    }

    public void updateManualStatus(User user) {
        user.updateManualStatus();
        userRepository.save(user);
    }

    @Transactional
    public void delete(User user) {
        userPolicyRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }

    @Transactional
    public void addPolicyDummy(User user){
        Policy policy = policyRepository.findById(1L).get();
        Policy policy2 = policyRepository.findById(2L).get();
        Policy policy3 = policyRepository.findById(3L).get();
        Policy policy4 = policyRepository.findById(4L).get();
        Policy policy5 = policyRepository.findById(5L).get();
        Policy policy6 = policyRepository.findById(6L).get();
        Policy policy7 = policyRepository.findById(7L).get();
        Policy policy8 = policyRepository.findById(8L).get();
        Policy policy9 = policyRepository.findById(9L).get();
        Policy policy10 = policyRepository.findById(10L).get();

        Policy policy11 = policyRepository.findById(11L).get();
        Policy policy12 = policyRepository.findById(12L).get();
        Policy policy13 = policyRepository.findById(13L).get();
        Policy policy14 = policyRepository.findById(14L).get();
        Policy policy15 = policyRepository.findById(15L).get();
        Policy policy16 = policyRepository.findById(16L).get();
        Policy policy17 = policyRepository.findById(17L).get();
        Policy policy18 = policyRepository.findById(18L).get();
        Policy policy19 = policyRepository.findById(19L).get();
        Policy policy20 = policyRepository.findById(20L).get();

        List<Policy> interestPolices = List.of(policy, policy2, policy3, policy4, policy5, policy6, policy7, policy8, policy9 , policy10);
        List<Policy> appliyPolices = List.of(policy11, policy12, policy13, policy14, policy15, policy16, policy17, policy18, policy19 , policy20);

        for (Policy interestPolicy : interestPolices) {
            UserPolicy userPolicy = UserPolicy.builder()
                    .user(user)
                    .policy(interestPolicy)
                    .interestFlag(true)
                    .applyFlag(false)
                    .build();
            userPolicyRepository.save(userPolicy);
        }

        for (Policy applyPolicy : appliyPolices) {
            UserPolicy userPolicy = UserPolicy.builder()
                    .user(user)
                    .policy(applyPolicy)
                    .interestFlag(false)
                    .applyFlag(true)
                    .build();
            userPolicyRepository.save(userPolicy);
        }

    }

    public List<HomeInfoResponseDto.HomePolicyInfo> getRecommendPolicyDummy() {
        Policy loansPolicy = policyRepository.findAllBySupportPolicyType(SupportPolicyType.LOANS).get(0);
        Policy moneyPolicy = policyRepository.findAllBySupportPolicyType(SupportPolicyType.MONEY).get(0);
        Policy socialServicePolicy = policyRepository.findAllBySupportPolicyType(SupportPolicyType.SOCIAL_SERVICE).get(0);

        List<HomeInfoResponseDto.HomePolicyInfo> result = new ArrayList<>();
        result.add(get(loansPolicy));
        result.add(get(moneyPolicy));
        result.add(get(socialServicePolicy));
        return result;
    }

    public HomeInfoResponseDto.HomePolicyInfo get(Policy policy) {
        return HomeInfoResponseDto.HomePolicyInfo.builder()
                .policyId(policy.getId())
                .policyName(policy.getPolicyName())
                .policyLogo(policy.getPolicyLogo())
                .supportPolicyTypeName(policy.getSupportPolicyType().getDescription())
                .supportPolicyType(policy.getSupportPolicyType())
                .build();
    }
}
