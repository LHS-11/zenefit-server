package com.cmc.zenefitserver.global.infra.notification.dao;

import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.infra.notification.dto.NotificationListInfoResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmc.zenefitserver.domain.policy.domain.QPolicy.policy;
import static com.cmc.zenefitserver.global.infra.notification.domain.QNotification.notification;

@RequiredArgsConstructor
@Repository
public class NotificationQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<NotificationListInfoResponseDto> searchNotificationBySlice(User user, Pageable pageable) {
        JPAQuery<NotificationListInfoResponseDto> query = jpaQueryFactory.select(
                        Projections.constructor(NotificationListInfoResponseDto.class,
                                notification.id,
                                notification.title,
                                notification.content,
                                notification.image,
                                notification.searchDateType,
                                notification.policyId
                        )
                )
                .from(notification)
                .where(
                        notification.user.userId.eq(user.getUserId())
                );

        List<NotificationListInfoResponseDto> results = query.orderBy(notification.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

//        return checkLastPage(pageable, results);
        return PageableExecutionUtils.getPage(results, pageable, query::fetchCount);
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
