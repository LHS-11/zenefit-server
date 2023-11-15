package com.cmc.zenefitserver.domain.policy.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PolicyDateContent {

    private String dateContent; // 사업 신청 기간
    private String content; // 비고
}
