package com.cmc.zenefitserver.domain.user.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Api(tags="TestController", description = "API for Testing")
public class TestController {

    @GetMapping
    @ApiOperation(value = "Test API",notes = "이 API 는 테스트용입니다.")
    public String test(){
        return "Success";
    }
}
