package com.cmc.zenefitserver.global.infra.notification.application;

import com.cmc.zenefitserver.domain.user.dao.UserRepository;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.infra.fcm.FCMService;
import com.cmc.zenefitserver.global.infra.notification.dao.NotificationQueryRepository;
import com.cmc.zenefitserver.global.infra.notification.dao.NotificationRepository;
import com.cmc.zenefitserver.global.infra.notification.dto.NotificationListInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;

    private final UserRepository userRepository;
    private final FCMService fcmService;


    public Page<NotificationListInfoResponseDto> findAllNotification(User user, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return notificationQueryRepository.searchNotificationBySlice(user, pageable);
    }

    public void notifyAllTest(){
        List<User> findUsers = userRepository.findAll()
                .stream()
                .filter(user -> user.getFcmToken() != null)
                .collect(Collectors.toList());

        fcmService.sendFCMNotificationMulticast(findUsers,"test","test","test");
    }

}
