package com.cmc.zenefitserver.global.infra.notification.application;

import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.infra.notification.dao.NotificationQueryRepository;
import com.cmc.zenefitserver.global.infra.notification.dao.NotificationRepository;
import com.cmc.zenefitserver.global.infra.notification.dto.NotificationListInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;

    public Slice<NotificationListInfoResponseDto> findAllNotification(User user, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return notificationQueryRepository.searchNotificationBySlice(user, pageable);
    }

}
