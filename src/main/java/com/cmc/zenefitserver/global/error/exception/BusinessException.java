package com.cmc.zenefitserver.global.error.exception;


import com.cmc.zenefitserver.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * 비즈니스 로직 예외 처리 부모 클래스
 */
@Getter
public class BusinessException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;
    private HttpStatus httpStatus;
    private Map<String,String> data;

    public BusinessException(ErrorCode errorCode){
        super();
        this.errorCode=errorCode;
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getStatus();
    }

    public BusinessException(ErrorCode errorCode, Map<String,String> data){
        super();
        this.errorCode=errorCode;
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getStatus();
        this.data=data;
    }
}
