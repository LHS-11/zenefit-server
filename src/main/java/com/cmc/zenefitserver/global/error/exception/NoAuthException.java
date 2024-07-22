package com.cmc.zenefitserver.global.error.exception;

import com.cmc.zenefitserver.global.error.ErrorCode;

import java.util.Map;

public class NoAuthException extends CustomException{
    public NoAuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NoAuthException(ErrorCode errorCode, Map<String, String> data) {
        super(errorCode, data);
    }
}
