package com.cmc.zenefitserver.domain.job.domain;

public enum JobType {

    EMPLOYED, // 재직자
    SELF_EMPLOYED, // 자영업자
    UNEMPLOYED, // 미취업자
    FREELANCER, // 프리랜서
    DAILY_WORKER, // 일용근로자
    ENTREPRENEUR, // (예비) 창업자
    SHORT_TERM_WORKER, // 단기근로자
    FARMER, // 영농종사자
    UNLIMITED; // 제한없음
}
