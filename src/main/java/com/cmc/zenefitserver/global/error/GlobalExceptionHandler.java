package com.cmc.zenefitserver.global.error;

import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.annotation.AuthUser;
import com.cmc.zenefitserver.global.common.CommonResponse;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private void getExceptionStackTrace(Exception e, @AuthUser User user,
                                        HttpServletRequest request) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.append("\n==========================!!!TRACE START!!!==========================\n");
        pw.append("uri: " + request.getRequestURI() + " " + request.getMethod() + "\n");
        if (user != null) {
            pw.append("uid: " + user.getUserId() + "\n");
        }
        pw.append(e.getMessage());
        pw.append("\n==================================================================\n");
        log.error(sw.toString());
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity handleBusinessException(BusinessException e,@AuthUser User user,HttpServletRequest request){
        getExceptionStackTrace(e, user, request);
        ErrorCode errorCode = e.getErrorCode();
        return new ResponseEntity<>(CommonResponse.failure(errorCode.getCode(),errorCode.getMessage(),e.getData()),errorCode.getStatus());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleUnknownException(Exception e,@AuthUser User user,HttpServletRequest request){
        getExceptionStackTrace(e,user,request);
        return new ResponseEntity<>(CommonResponse.failure("500", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
