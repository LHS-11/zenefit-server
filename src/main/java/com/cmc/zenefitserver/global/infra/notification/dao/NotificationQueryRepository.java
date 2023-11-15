package com.cmc.zenefitserver.global.infra.notification.dao;

import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.infra.notification.dto.NotificationListInfoResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmc.zenefitserver.domain.policy.domain.QPolicy.policy;
import static com.cmc.zenefitserver.domain.userpolicy.domain.QUserPolicy.userPolicy;
import static com.cmc.zenefitserver.global.infra.notification.domain.QNotification.notification;

@RequiredArgsConstructor
@Repository
public class NotificationQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Slice<NotificationListInfoResponseDto> searchNotificationBySlice(User user, Long lastNotificationId, Pageable pageable) {
        List<NotificationListInfoResponseDto> results = jpaQueryFactory.select(
                        Projections.constructor(NotificationListInfoResponseDto.class,
                                notification.id,
                                notification.title,
                                notification.content,
                                notification.image
                        )
                )
                .from(notification)
                .where(
                        ltNotificationId(lastNotificationId),
                        userPolicy.user.userId.eq(user.getUserId())
                )
                .orderBy(userPolicy.policy.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, results);
    }

    private BooleanExpression ltNotificationId(Long policyId) {
        if (policyId == null) {
            return null;
        }
        return policy.id.lt(policyId);
    }

    // 무한 스크롤 방식 처리하는 메서드
    private Slice<NotificationListInfoResponseDto> checkLastPage(Pageable pageable, List<NotificationListInfoResponseDto> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }


}
