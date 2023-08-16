package com.cmc.zenefitserver.global.infra.notification.api;

import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.annotation.AuthUser;
import com.cmc.zenefitserver.global.common.CommonResponse;
import com.cmc.zenefitserver.global.infra.notification.application.NotificationService;
import com.cmc.zenefitserver.global.infra.notification.dto.NotificationListInfoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/notify")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "유저의 알림 내역 조회 API", description = "유저의 알림 내역을 조회합니다.")
    public CommonResponse<Slice<NotificationListInfoResponseDto>> findAllNotification(@AuthUser User user, @RequestParam Long lastNotificationId, Pageable pageable){
        log.info("알림 내역 조회 API, user = {}", user.getUserId());
        return CommonResponse.success(notificationService.findAllNotification(user,lastNotificationId,pageable));
    }

}
