package com.cmc.zenefitserver.global.infra.notification.dao;

import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.infra.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findAllByUser_UserId(Long userId);

    void deleteAllByUser(User user);
}
