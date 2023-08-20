package com.cmc.zenefitserver.global.error.exception;

import com.cmc.zenefitserver.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Getter
public class ControllerException extends RuntimeException {

    private ErrorCode errorCode;
    private String message;
    private HttpStatus httpStatus;
    private Map<String, String> data;

    public ControllerException(BindingResult bindingResult) {
        super();
        this.message = bindingResult.getFieldError().getDefaultMessage();
    }

//    public ControllerException(ErrorCode errorCode){
//        super();
//        this.errorCode=errorCode;
//        this.message = errorCode.getMessage();
//        this.httpStatus = errorCode.getStatus();
//    }
//
//    public ControllerException(ErrorCode errorCode, Map<String,String> data){
//        super();
//        this.errorCode=errorCode;
//        this.message = errorCode.getMessage();
//        this.httpStatus = errorCode.getStatus();
//        this.data=data;
//    }
}
