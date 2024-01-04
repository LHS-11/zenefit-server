package com.cmc.zenefitserver.global.infra.notification.domain;

import com.cmc.zenefitserver.domain.policy.domain.enums.SearchDateType;
import lombok.Getter;

@Getter
public enum NotificationType {

    APPLY_END_DATE_D_DAY_ONE("신청마감일 D-1", "내일이 신청 마감일이에요.\n서둘러 신청하세요!", SearchDateType.END_DATE),
    APPLY_END_DATE_D_DAY_THREE("신청마감일 D-3", "신청일이 얼마 남지 않았어요.\n서둘러 신청하세요!", SearchDateType.END_DATE),
    APPLY_END_DATE_D_DAY_SEVEN("신청마감일 D-7", "일주일 뒤 신청이 마감돼요.", SearchDateType.END_DATE),
    APPLY_STT_DATE_D_DAY_ONE("신청시작일 D-1", "내일부터 신청이 시작돼요!", SearchDateType.STT_DATE),
    APPLY_STT_DATE_D_DAY_THREE("신청시작일 D-3", "신청시작일이 얼마 남지 않았어요.", SearchDateType.STT_DATE),
    APPLY_STT_DATE_D_DAY_SEVEN("신청시작일 D-7", "일주일 뒤 신청이 시작돼요.", SearchDateType.STT_DATE);

    final String title;
    final String content;
    final SearchDateType searchDateType;

    NotificationType(String title, String content, SearchDateType searchDateType) {
        this.title = title;
        this.content = content;
        this.searchDateType = searchDateType;
    }
}
