package com.cmc.zenefitserver.global.auth.jwt;

import org.json.simple.JSONObject;
import com.cmc.zenefitserver.global.error.ErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequestMapping
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");

        // 토큰이 없을 경우
        if(exception==null || exception.equals(ErrorCode.NOT_FOUND_TOKEN.getCode())){
            setResponse(response,ErrorCode.NOT_FOUND_TOKEN);
            return;
        }

        // 잘못된 타입의 토큰일 경우
        if(exception.equals(ErrorCode.INVALID_AUTH_TOKEN.getCode())){
            setResponse(response,ErrorCode.INVALID_AUTH_TOKEN);
            return;
        }

        // 잘못된 서명
        if(exception.equals(ErrorCode.INVALID_SIGNATURE_TOKEN.getCode())){
            setResponse(response,ErrorCode.INVALID_SIGNATURE_TOKEN);
            return;
        }

        // 토큰 만료된 경우
        if(exception.equals(ErrorCode.EXPIRED_TOKEN.getCode())){
            setResponse(response,ErrorCode.EXPIRED_TOKEN);
            return;
        }

        // 지원되지 않는 토큰인 경우
        if(exception.equals(ErrorCode.UNSUPPORTED_TOKEN.getCode())){
            setResponse(response,ErrorCode.UNSUPPORTED_TOKEN);
            return;
        }

    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        JSONObject json = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        json.put("code", errorCode.getCode());
        json.put("isSuccess", false);
        json.put("message", errorCode.getMessage());
        response.getWriter().print(json);

    }
}
