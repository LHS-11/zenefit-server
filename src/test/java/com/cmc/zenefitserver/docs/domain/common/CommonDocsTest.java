package com.cmc.zenefitserver.docs.domain.common;

import com.cmc.zenefitserver.docs.RestDocsSupport;
import com.cmc.zenefitserver.docs.utils.CustomResponseFieldsSnippet;
import com.cmc.zenefitserver.global.common.CommonResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommonDocsTest extends RestDocsSupport {


    @Override
    protected Object initController() {
        return new EnumViewController();
    }

    @DisplayName("공통코드를 테스트합니다.")
    @Test
    void commonsTest() throws Exception {
        // given

        // when
        ResultActions result = this.mockMvc.perform(
                get("/docs")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Access Token")
        );

        MvcResult mvcResult = result.andReturn();
        Docs docs = getData(mvcResult);

        // then
        result.andExpect(status().isOk())
                .andDo(document(
                        "common",
                        requestHeaders(headerWithName("Authorization").description("API 인증 Access Token")),
                        customResponseFields("custom-response", null,
                                attributes(key("title").value("공통응답")),
                                subsectionWithPath("result").description("데이터"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
                        ),
                        customResponseFields("custom-response", beneathPath("result.errorCodes").withSubsectionId("errorCode"),
                                attributes(key("title").value("에러코드")),
                                enumConvertFieldDescriptor(docs.getErrorCodes())
                        ),
                        customResponseFields("custom-response", beneathPath("result.areaCodes").withSubsectionId("areaCode"),
                                attributes(key("title").value("시/구 코드")),
                                enumConvertFieldDescriptor(docs.getAreaCodes())
                        ),
                        customResponseFields("custom-response", beneathPath("result.cityCodes").withSubsectionId("cityCode"),
                                attributes(key("title").value("시/도 코드")),
                                enumConvertFieldDescriptor(docs.getCityCodes())
                        ),
                        customResponseFields("custom-response", beneathPath("result.educationTypes").withSubsectionId("educationType"),
                                attributes(key("title").value("학력 코드")),
                                enumConvertFieldDescriptor(docs.getEducationTypes())
                        ),
                        customResponseFields("custom-response", beneathPath("result.jobTypes").withSubsectionId("jobType"),
                                attributes(key("title").value("직업 코드")),
                                enumConvertFieldDescriptor(docs.getJobTypes())
                        ),
                        customResponseFields("custom-response", beneathPath("result.genders").withSubsectionId("gender"),
                                attributes(key("title").value("성별")),
                                enumConvertFieldDescriptor(docs.getGenders())
                        ),
                        customResponseFields("custom-response", beneathPath("result.providerTypes").withSubsectionId("providerType"),
                                attributes(key("title").value("소셜 로그인 제공처")),
                                enumConvertFieldDescriptor(docs.getProviderTypes())
                        ),
                        customResponseFields("custom-response", beneathPath("result.characters").withSubsectionId("character"),
                                attributes(key("title").value("유저 캐릭터")),
                                enumConvertFieldDescriptor(docs.getCharacters())
                        ),
                        customResponseFields("custom-response", beneathPath("result.supportPolicyTypes").withSubsectionId("supportPolicyType"),
                                attributes(key("title").value("지원 정책 유형")),
                                enumConvertFieldDescriptor(docs.getSupportPolicyTypes())
                        ),
                        customResponseFields("custom-response", beneathPath("result.policyDateTypes").withSubsectionId("policyDateType"),
                                attributes(key("title").value("정책 신청 기간 타입")),
                                enumConvertFieldDescriptor(docs.getPolicyDateTypes())
                        ),
                        customResponseFields("custom-response", beneathPath("result.policyMethodTypes").withSubsectionId("policyMethodType"),
                                attributes(key("title").value("정책 신청 방법")),
                                enumConvertFieldDescriptor(docs.getPolicyMethodTypes())
                        ),
                        customResponseFields("custom-response", beneathPath("result.policyTypes").withSubsectionId("policyType"),
                                attributes(key("title").value("정책 분야")),
                                enumConvertFieldDescriptor(docs.getPolicyTypes())
                        )
                ));
    }

    private static FieldDescriptor[] enumConvertFieldDescriptor(Map<String, String> enumValues) {
        return enumValues.entrySet().stream()
                .map(x -> fieldWithPath(x.getKey()).description(x.getValue()))
                .toArray(FieldDescriptor[]::new);
    }

    private Docs getData(MvcResult result) throws IOException {
        CommonResponse<Docs> commonResponse = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                new TypeReference<CommonResponse<Docs>>() {
                });
        return commonResponse.getData();
    }

    public static CustomResponseFieldsSnippet customResponseFields(String type, PayloadSubsectionExtractor<?> subsectionExtractor,
                                                                   Map<String, Object> attribute, FieldDescriptor... descriptors) {
        return new CustomResponseFieldsSnippet(type, Arrays.asList(descriptors), attribute, true, subsectionExtractor);
    }
}
