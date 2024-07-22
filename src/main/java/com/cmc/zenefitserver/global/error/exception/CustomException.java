package com.cmc.zenefitserver.global.error.exception;

import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public abstract class CustomException extends RuntimeException {

    private ErrorCode errorCode;
    private String message;
    private HttpStatus httpStatus;
    private Map<String, String> data;

    public CustomException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getStatus();
    }

    public CustomException(ErrorCode errorCode, Map<String, String> data) {
        super();
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getStatus();
        this.data = data;
    }

    public void setData(User user) {
        this.data.put("userId", String.valueOf(user.getUserId()));
    }
}
