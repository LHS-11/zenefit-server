package com.cmc.zenefitserver.docs.domain.user.api;

import com.cmc.zenefitserver.docs.RestDocsSupport;
import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import com.cmc.zenefitserver.domain.user.api.UserController;
import com.cmc.zenefitserver.domain.user.application.UserService;
import com.cmc.zenefitserver.domain.user.domain.EducationType;
import com.cmc.zenefitserver.domain.user.domain.JobType;
import com.cmc.zenefitserver.domain.user.dto.request.SignUpRequestDto;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Set;

import static com.cmc.zenefitserver.docs.utils.DocumentLinkGenerator.DocUrl.*;
import static com.cmc.zenefitserver.docs.utils.DocumentLinkGenerator.generateLinkCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);

    @Override
    protected Object initController() {
        return new UserController(userService);
    }

    @DisplayName("회원가입을 하는 API")
    @Test
    void signUpTest() throws Exception {

        // given
        SignUpRequestDto request = createSignUpRequestDto();
        String accessToken = "Test AccessToken";
        String refreshToken = "Test RefreshToken";
        String nickName = "cp3";

        String jsonRequest = "{\n" +
                "    \"userId\" : \"3001\",\n" +
                "  \"age\": 25,\n" +
                "  \"areaCode\" : \"서울\",\n" +
                "  \"cityCode\" : \"강서구\",\n" +
                "  \"lastYearIncome\": 50000000000,\n" +
                "  \"educationType\": \"대학 재학\",\n" +
                "  \"jobs\": [\n" +
                "      \"재직자\", \n" +
                "      \"창업자\"\n" +
                "  ],\n" +
                "  \"marketingStatus\" : true\n" +
                "}";

        when(userService.signUp(any(SignUpRequestDto.class)))
                .thenReturn(TokenResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .nickname(nickName)
                        .build());

        // when & then
        mockMvc.perform(patch("/user/signup")
//                        .content(objectMapper.writeValueAsString(request))
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-signup",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("userId").type(JsonFieldType.STRING).description("유저 ID"),
                                        fieldWithPath("age").type(JsonFieldType.NUMBER).description("나이"),
                                        fieldWithPath("areaCode").type(JsonFieldType.STRING).description(generateLinkCode(AREA_CODE)),
                                        fieldWithPath("cityCode").type(JsonFieldType.STRING).description(generateLinkCode(CITY_CODE)),
                                        fieldWithPath("lastYearIncome").type(JsonFieldType.NUMBER).description("작년 소득"),
                                        fieldWithPath("educationType").type(JsonFieldType.STRING).description(generateLinkCode(EDUCATION_TYPE)),
                                        fieldWithPath("jobs").type(JsonFieldType.ARRAY).description(generateLinkCode(JOB_TYPE)),
                                        fieldWithPath("marketingStatus").type(JsonFieldType.BOOLEAN).description("마켓팅 동의 여부")
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                        fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                        fieldWithPath("result.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                                        fieldWithPath("result.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("유저 닉네임")

                                )
                        )
                );


        // then
    }

    private static SignUpRequestDto createSignUpRequestDto() {
        return SignUpRequestDto.builder()
                .userId("5001")
                .age(25)
                .areaCode(AreaCode.findAreaCodeByName("서울"))
                .cityCode(CityCode.GANGSEO)
                .lastYearIncome(Double.valueOf(50000000000L))
                .educationType(EducationType.COLLEGE_GRADUATE)
                .jobs(Set.of(JobType.EMPLOYED, JobType.ENTREPRENEUR))
                .marketingStatus(true)
                .build();
    }
}