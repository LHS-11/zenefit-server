package com.cmc.zenefitserver.global.infra.notification.application;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.SearchDateType;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.userpolicy.dao.UserPolicyRepository;
import com.cmc.zenefitserver.global.infra.fcm.FCMService;
import com.cmc.zenefitserver.global.infra.notification.dao.NotificationRepository;
import com.cmc.zenefitserver.global.infra.notification.domain.Notification;
import com.cmc.zenefitserver.global.infra.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationScheduler {

    private final PolicyRepository policyRepository;
    private final UserPolicyRepository userPolicyRepository;
    private final AmazonS3Client amazonS3Client;
    private final FCMService fcmService;
    private final NotificationRepository notificationRepository;


    @Scheduled(cron = "0 1 0 1/1 * ?")// 10분 주기로 실행
    public void notifyUser() {
        System.out.println("LocalDate.now() = " + LocalDate.now());
        notifyUserBySttDate(LocalDate.now()); // 신청 시작일 기준
        notifyUserByEndDate(LocalDate.now()); // 신청 종료일 기준
//        notifyUserByApplyDate(LocalDate.now()); 신청일 기준
    }


    public void notifyUserByEndDate(LocalDate now) {

        // D-1
        notifyUser(
                policyRepository.findAllByApplyEndDate(now.plusDays(1)),
                NotificationType.APPLY_END_DATE_D_DAY_ONE.getTitle(),
                NotificationType.APPLY_END_DATE_D_DAY_ONE.getContent(),
                NotificationType.APPLY_END_DATE_D_DAY_ONE.getImageUrl(),
                SearchDateType.END_DATE
        );
        // D-3
        notifyUser(
                policyRepository.findAllByApplyEndDate(now.plusDays(3)),
                NotificationType.APPLY_END_DATE_D_DAY_THREE.getTitle(),
                NotificationType.APPLY_END_DATE_D_DAY_THREE.getContent(),
                NotificationType.APPLY_END_DATE_D_DAY_THREE.getImageUrl(),
                SearchDateType.END_DATE
        );
        // D-7
        notifyUser(
                policyRepository.findAllByApplyEndDate(now.plusDays(7)),
                NotificationType.APPLY_END_DATE_D_DAY_SEVEN.getTitle(),
                NotificationType.APPLY_END_DATE_D_DAY_SEVEN.getContent(),
                NotificationType.APPLY_END_DATE_D_DAY_SEVEN.getImageUrl(),
                SearchDateType.END_DATE
        );
    }

    public void notifyUserBySttDate(LocalDate now) {

        // D-1
        notifyUser(
                policyRepository.findAllByApplySttDate(now.plusDays(1)),
                NotificationType.APPLY_STT_DATE_D_DAY_ONE.getTitle(),
                NotificationType.APPLY_STT_DATE_D_DAY_ONE.getContent(),
                NotificationType.APPLY_STT_DATE_D_DAY_ONE.getImageUrl(),
                SearchDateType.STT_DATE
        );
        // D-3
        notifyUser(
                policyRepository.findAllByApplySttDate(now.plusDays(3)),
                NotificationType.APPLY_STT_DATE_D_DAY_THREE.getTitle(),
                NotificationType.APPLY_STT_DATE_D_DAY_THREE.getContent(),
                NotificationType.APPLY_STT_DATE_D_DAY_THREE.getImageUrl(),
                SearchDateType.STT_DATE
        );
        // D-7
        notifyUser(
                policyRepository.findAllByApplySttDate(now.plusDays(7)),
                NotificationType.APPLY_STT_DATE_D_DAY_SEVEN.getTitle(),
                NotificationType.APPLY_END_DATE_D_DAY_SEVEN.getContent(),
                NotificationType.APPLY_END_DATE_D_DAY_SEVEN.getImageUrl(),
                SearchDateType.STT_DATE
        );
    }

    //    public void notifyUserByApplyDate(LocalDate now) {
//        String imageUrl = getImageUrl(SearchDateType.STT_DATE);
//        // D-1
//        notifyUser(
//                policyRepository.findAllBySttDate(now.plusDays(1)),
//                "신청일 D-1",
//                "내일이 신청하는 날이예요!",
//                imageUrl
//        );
//        // D-3
//        notifyUser(
//                policyRepository.findAllBySttDate(now.plusDays(3)),
//                "신청일 D-3",
//                "신청일이 얼마 남지 않았어요.",
//                imageUrl
//        );
//        // D-7
//        notifyUser(
//                policyRepository.findAllBySttDate(now.plusDays(7)),
//                "신청일 D-7",
//                "신청일이 일주일 남았어요.",
//                imageUrl
//        );
//
//    }
//
    private void notifyUser(List<Policy> policies, String titlePrefix, String content, String imageUrl, SearchDateType searchDateType) {
        for (Policy policy : policies) {
            String policyName = policy.getPolicyName();
            String title = "[" + policyName + "] " + titlePrefix;
            List<User> users = userPolicyRepository.findAllByPolicy_idAndInterestFlag(policy.getId(), true)
                    .stream()
                    .filter(up -> up.getUser().isPushNotificationStatus())
                    .map(up -> up.getUser())
                    .collect(Collectors.toList());

            fcmService.sendFCMNotificationMulticast(users, policy, title, content, imageUrl);

            List<Notification> notifications = users.stream()
                    .map(user ->
                            Notification.builder()
                                    .user(user)
                                    .title(title)
                                    .content(content)
                                    .image(imageUrl)
                                    .searchDateType(searchDateType)
                                    .policyId(policy.getId())
                                    .build()
                    )
                    .collect(Collectors.toList());

            notificationRepository.saveAll(notifications);
        }
    }


//    public String getImageUrl(SearchDateType searchDateType) {
//
//        String bucketName = "zenefit-bucket";
//        String folderName = "alarm";
//
//        String bucketImageUrl = searchDateType.name();
//
//        String s3ObjectKey = folderName + "/" + bucketImageUrl + ".png"; // 이미지 파일의 객체 키
//
//        Date expiration = new Date(System.currentTimeMillis() + 3600000); // URL의 만료 시간 (1시간)
//        GeneratePresignedUrlRequest generatePresignedUrlRequest =
//                new GeneratePresignedUrlRequest(bucketName, s3ObjectKey).withExpiration(expiration);
//
//        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
//        return url.toString(); // 이미지 URL 반환
//    }

}
