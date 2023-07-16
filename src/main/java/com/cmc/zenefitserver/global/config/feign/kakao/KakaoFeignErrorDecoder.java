package com.cmc.zenefitserver.global.config.feign.kakao;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@RequiredArgsConstructor(staticName = "of")
public class KakaoFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.resolve(response.status());
        if(httpStatus== (HttpStatus.NOT_FOUND)){
            log.info("[FeignErrorDecoder] Http Status {}",httpStatus);
        }
        if(httpStatus== (HttpStatus.BAD_REQUEST)) {
            log.info("[FeignErrorDecoder] Http Status {}", httpStatus);
        }

        return errorDecoder.decode(methodKey,response);
    }
}
