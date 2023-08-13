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
    INVALID_AUTH_TOKEN("AUTH_004", "권한 정보가 없는 토큰입니다.", UNAUTHORIZED),
    UNSUPPORTED_TOKEN("AUTH_005", "지원하지 않는 토큰입니다.", UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("AUTH_006", "리프레시 토큰이 유효하지 않습니다.", UNAUTHORIZED),
    MISMATCH_REFRESH_TOKEN("AUTH_007", "데이터베이스에 있는 리프레시 토큰과 다릅니다.", UNAUTHORIZED),
    EXPIRED_REFRESH_TOKEN("AUTH_008", "리프레시 토큰 기한이 만료되었습니다.", UNAUTHORIZED),
    FORBIDDEN_USER("AUTH_009", "권한이 없는 유저입니다.", UNAUTHORIZED),
    FAIL_TO_MAKE_APPLE_PUBLIC_KEY("AUTH_010", "애플 공개키 생성에 실패했습니다.", UNAUTHORIZED),
    MISMATCH_APPLE_KEY("AUTH_011", "애플 공개키와 맞지 않습니다.", UNAUTHORIZED),
    MISMATCH_ISSUER("AUTH_012", "애플에서 제공한 발행처와 맞지 않습니다.", UNAUTHORIZED),
    MISMATCH_AUDIENCE("AUTH_013", "애플에서 제공한 수신자와 맞지 않습니다.", UNAUTHORIZED),


    /**
     * USER 관련 오류
     */
    NOT_FOUND_USER("USER_001", "데이터베이스에 없는 유저입니다.", UNAUTHORIZED),
    DUPLICATE_NICKNAME("USER_002", "중복된 닉네임 입니다.", UNAUTHORIZED),
    DUPLICATE_EMAIL_PROVIDER("USER_003", "중복된 이메일과 소셜 타입입니다.", UNAUTHORIZED),
    INVALID_NICKNAME("USER_004", "닉네임은 1~7자의 영문 대소문자, 숫자, 한글로만 입력해주세요.", UNAUTHORIZED),

    /**
     * POLICY 관련 오류
     */
    NOT_FOUND_POLICY("POLICY_001", "데이터베이스에 없는 정책입니다.", UNAUTHORIZED),

    /**
     * USER POLICY 관련 오류
     */
    NOT_FOUND_USER_POLICY("USER_POLICY_001", "데이터베이스에 없는 유저 정책입니다.", UNAUTHORIZED),



    /**
     * Common 관련 오류
     */
    NOT_FOUND_GENDER_ENUM_VALUE("COMMON_001", "gender 값이 올바르지 않은 값입니다.", UNAUTHORIZED),
    NOT_FOUND_EDUCATION_ENUM_VALUE("COMMON_002", "education 값이 올바르지 않은 값입니다.", UNAUTHORIZED),
    NOT_FOUND_JOB_ENUM_VALUE("COMMON_003", "job 이 올바르지 않은 값입니다.", UNAUTHORIZED),
    NOT_FOUND_SPLZ_ENUM_VALUE("COMMON_004", "splzRlmRqisCn 이 올바르지 않은 값입니다.", UNAUTHORIZED),
    NOT_FOUND_AREA_ENUM_VALUE("COMMON_005", "area 값이 올바르지 않은 값입니다.", UNAUTHORIZED),
    NOT_FOUND_PROVIDER_TYPE_ENUM_VALUE("COMMON_006", "providerType 값이 올바르지 않은 값입니다.", UNAUTHORIZED),
    NOT_FOUND_SUPPORT_POLICY_TYPE_ENUM_VALUE("COMMON_007", "supportPolicyType 값이 올바르지 않은 값입니다.", UNAUTHORIZED),
    NOT_FOUND_POLICY_TYPE_ENUM_VALUE("COMMON_008", "policyType 값이 올바르지 않은 값입니다.", UNAUTHORIZED);;



    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(final String code, final String message, final HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
