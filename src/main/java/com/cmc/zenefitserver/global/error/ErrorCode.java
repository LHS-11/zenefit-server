package com.cmc.zenefitserver.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    /**
     * AUTH 관련 오류
     */
    NOT_FOUND_TOKEN("AUTH_001", "토큰을 찾을 수 없습니다.", UNAUTHORIZED),
    EXPIRED_TOKEN("AUTH_002", "토큰이 만료되었습니다.", UNAUTHORIZED),
    INVALID_SIGNATURE_TOKEN("AUTH_003", "토큰의 서명이 유효하지 않습니다.", UNAUTHORIZED),
    INVALID_AUTH_TOKEN("AUTH_004","권한 정보가 없는 토큰입니다.", UNAUTHORIZED),
    UNSUPPORTED_TOKEN("AUTH_005","지원하지 않는 토큰입니다.",UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("AUTH_006","리프레시 토큰이 유효하지 않습니다.",UNAUTHORIZED),
    MISMATCH_REFRESH_TOKEN("AUTH_007","데이터베이스에 있는 리프레시 토큰과 다릅니다.",UNAUTHORIZED),
    EXPIRED_REFRESH_TOKEN("AUTH_008","리프레시 토큰 기한이 만료되었습니다.",UNAUTHORIZED),
    FORBIDDEN_USER("AUTH_009", "권한이 없는 유저입니다.", UNAUTHORIZED),
    NOT_FOUND_USER("AUTH_010", "데이터베이스에 없는 유저입니다.", UNAUTHORIZED);


    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(final String code,final String message,final HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
