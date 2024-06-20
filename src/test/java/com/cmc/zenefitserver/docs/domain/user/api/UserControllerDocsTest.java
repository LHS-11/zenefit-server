package com.cmc.zenefitserver.docs.domain.user.api;

import com.cmc.zenefitserver.docs.RestDocsSupport;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.*;
import com.cmc.zenefitserver.domain.user.api.UserController;
import com.cmc.zenefitserver.domain.user.application.UserService;
import com.cmc.zenefitserver.domain.user.domain.*;
import com.cmc.zenefitserver.domain.user.domain.Character;
import com.cmc.zenefitserver.domain.user.dto.request.ModifyRequestDto;
import com.cmc.zenefitserver.domain.user.dto.request.SignUpRequestDto;
import com.cmc.zenefitserver.domain.user.dto.response.*;
import com.cmc.zenefitserver.global.auth.ProviderType;
import com.cmc.zenefitserver.global.auth.jwt.UserAdapter;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cmc.zenefitserver.docs.utils.DocumentLinkGenerator.DocUrl.*;
import static com.cmc.zenefitserver.docs.utils.DocumentLinkGenerator.generateLinkCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);

    @Override
    protected Object initController() {
        return new UserController(userService);
    }

    @DisplayName("회원가입 API 테스트")
    @Test
    void signUpDocsTest() throws Exception {

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
                .andDo(document("user-signup-patch",
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

    @DisplayName("회원 정보 수정 API 테스트")
    @Test
    @WithMockUser
    void modifyDocsTest() throws Exception {
        // given
        ModifyRequestDto modifyRequestDto = createModifyRequestDto();
        String jsonRequest = "{\n" +
                "  \"nickname\": \"test\",\n" +
                "  \"age\": 28,\n" +
                "  \"areaCode\" : \"경기\",\n" +
                "  \"cityCode\" : \"부천시\",\n" +
                "  \"lastYearIncome\": 50000000,\n" +
                "  \"educationType\": \"대학 재학\",\n" +
                "  \"jobs\": [\n" +
                "      \"재직자\",\n" +
                "      \"창업자\"\n" +
                "  ],\n" +
                "  \"userDetail\" : {\n" +
                "      \"gender\" : \"여성\",\n" +
                "      \"smallBusiness\" : true,\n" +
                "      \"soldier\" : true,\n" +
                "      \"lowIncome\" : true,\n" +
                "      \"disabled\" : true,\n" +
                "      \"localTalent\" : true,\n" +
                "      \"farmer\" : true\n" +
                "  }\n" +
                "}";

        User user = createUser();
        user.setUserId(12L);
        UserAdapter userAdapter = new UserAdapter(user);

        when(userService.modify(any(ModifyRequestDto.class), any(User.class)))
                .thenReturn(user);

        // when & then
        mockMvc.perform(patch("/user/modify")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "access token")
//                        .with(SecurityMockMvcRequestPostProcessors.user("testUser").roles("USER"))
//                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-modify-patch",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("API 인증 Access Token")),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 ID"),
                                fieldWithPath("age").type(JsonFieldType.NUMBER).description("나이"),
                                fieldWithPath("areaCode").type(JsonFieldType.STRING).description(generateLinkCode(AREA_CODE)),
                                fieldWithPath("cityCode").type(JsonFieldType.STRING).description(generateLinkCode(CITY_CODE)),
                                fieldWithPath("lastYearIncome").type(JsonFieldType.NUMBER).description("작년 소득"),
                                fieldWithPath("educationType").type(JsonFieldType.STRING).description(generateLinkCode(EDUCATION_TYPE)),
                                fieldWithPath("jobs").type(JsonFieldType.ARRAY).description(generateLinkCode(JOB_TYPE)),
                                fieldWithPath("userDetail").type(JsonFieldType.OBJECT).description("유저 상세 정보"),
                                fieldWithPath("userDetail.gender").type(JsonFieldType.STRING).description(generateLinkCode(GENDER)),
                                fieldWithPath("userDetail.smallBusiness").type(JsonFieldType.BOOLEAN).description("중소기업 여부"),
                                fieldWithPath("userDetail.soldier").type(JsonFieldType.BOOLEAN).description("군인 여부"),
                                fieldWithPath("userDetail.lowIncome").type(JsonFieldType.BOOLEAN).description("저소득층 여부"),
                                fieldWithPath("userDetail.disabled").type(JsonFieldType.BOOLEAN).description("장애인 여부"),
                                fieldWithPath("userDetail.localTalent").type(JsonFieldType.BOOLEAN).description("지역인재 여부"),
                                fieldWithPath("userDetail.farmer").type(JsonFieldType.BOOLEAN).description("농업인 여부")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.userId").type(JsonFieldType.NUMBER).description("유저 ID"),
                                fieldWithPath("result.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("result.age").type(JsonFieldType.NUMBER).description("나이"),
                                fieldWithPath("result.address").type(JsonFieldType.OBJECT).description("주소"),
                                fieldWithPath("result.address.areaCode").type(JsonFieldType.STRING).description(generateLinkCode(AREA_CODE)),
                                fieldWithPath("result.address.cityCode").type(JsonFieldType.STRING).description(generateLinkCode(CITY_CODE)),
                                fieldWithPath("result.lastYearIncome").type(JsonFieldType.NUMBER).description("작년 소득"),
                                fieldWithPath("result.educationType").type(JsonFieldType.STRING).description(generateLinkCode(EDUCATION_TYPE)),
                                fieldWithPath("result.jobs").type(JsonFieldType.ARRAY).description("직업 코드"),
                                fieldWithPath("result.policyCnt").type(JsonFieldType.NUMBER).description("가지고 있는 정책 수 => 삭제 검토"),
                                fieldWithPath("result.userDetail").type(JsonFieldType.OBJECT).description("유저 상세 정보"),
                                fieldWithPath("result.userDetail.id").type(JsonFieldType.NULL).description("유저 id => response DTO 검토"),
                                fieldWithPath("result.userDetail.gender").type(JsonFieldType.STRING).description(generateLinkCode(GENDER)),
                                fieldWithPath("result.userDetail.smallBusiness").type(JsonFieldType.BOOLEAN).description("중소기업 여부"),
                                fieldWithPath("result.userDetail.soldier").type(JsonFieldType.BOOLEAN).description("군인 여부"),
                                fieldWithPath("result.userDetail.lowIncome").type(JsonFieldType.BOOLEAN).description("저소득층 여부"),
                                fieldWithPath("result.userDetail.disabled").type(JsonFieldType.BOOLEAN).description("장애인 여부"),
                                fieldWithPath("result.userDetail.localTalent").type(JsonFieldType.BOOLEAN).description("지역인재 여부"),
                                fieldWithPath("result.userDetail.farmer").type(JsonFieldType.BOOLEAN).description("농업인 여부"),
                                fieldWithPath("result.fcmToken").type(JsonFieldType.STRING).description("fcm 토큰"),
                                fieldWithPath("result.pushNotificationStatus").type(JsonFieldType.BOOLEAN).description("푸시 알림 여부"),
                                fieldWithPath("result.appNotificationStatus").type(JsonFieldType.BOOLEAN).description("앱 내부 알림 여부 => 삭제 검토"),
                                fieldWithPath("result.provider").type(JsonFieldType.STRING).description(generateLinkCode(PROVIDER)),
                                fieldWithPath("result.benefit").type(JsonFieldType.NUMBER).description("정책을 통한 얻은 이득 => 삭제 검토"),
                                fieldWithPath("result.termsOfServiceStatus").type(JsonFieldType.BOOLEAN).description("마켓팅 동의 날짜"),
                                fieldWithPath("result.privacyStatus").type(JsonFieldType.BOOLEAN).description("개인정보처리방침 동의 여부"),
                                fieldWithPath("result.marketingStatus").type(JsonFieldType.BOOLEAN).description("마켓팅 동의 여부"),
                                fieldWithPath("result.marketingStatusDate").type(JsonFieldType.NULL).description("마켓팅 동의 날짜"),
                                fieldWithPath("result.manualStatus").type(JsonFieldType.BOOLEAN).description("메뉴얼 확인 여부"),
                                fieldWithPath("result.userRegistrationValid").type(JsonFieldType.BOOLEAN).description("뭔지 모르겠음 검토")
                        )

                ));
    }

    @DisplayName("시/도 조회 API 테스트")
    @Test
    void getAreaCodeDocsTest() throws Exception {

        List<String> result = AreaCode.getAreaCodeNameList();

        when(userService.getAreaCodes())
                .thenReturn(result);

        // when & then
        mockMvc.perform(get("/user/area")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-area-get",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.ARRAY).description("응답 결과"),
                                fieldWithPath("result[].").type(JsonFieldType.ARRAY).description(generateLinkCode(AREA_CODE))
                        )
                ));
    }

    @DisplayName("시/군/구 조회 API 테스트")
    @Test
    void getCityCodeDocsTest() throws Exception {
        // given
        String areaCodeName = AreaCode.SEOUL.getName();
        List<String> result = AreaCode.findCityCodes(areaCodeName);

        when(userService.getCityCodes(areaCodeName))
                .thenReturn(result);

        // when & then
        mockMvc.perform(get("/user/city")
                        .param("area", areaCodeName)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-city-get",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.ARRAY).description("응답 결과"),
                                fieldWithPath("result[].").type(JsonFieldType.ARRAY).description(generateLinkCode(CITY_CODE))
                        )
                ));
    }

    @DisplayName("소셜 로그인 정보 조회 API 테스트")
    @Test
    @WithMockUser
    void getSocialInfoDocsTest() throws Exception {
        // given
        User user = createUser();
        when(userService.getSocialInfo(any(User.class)))
                .thenReturn(SocialInfoResponseDto.builder()
                        .email(user.getEmail())
                        .provider(user.getProvider())
                        .build());

        // when &  then
        mockMvc.perform(get("/user/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "access token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-social-get",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("API 인증 Access Token")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.email").type(JsonFieldType.STRING).description("소셜 로그인 이메일"),
                                fieldWithPath("result.provider").type(JsonFieldType.STRING).description(generateLinkCode(PROVIDER))
                        )
                ));

    }

    @DisplayName("개인 유저 정보 조회 API 테스트")
    @Test
    @WithMockUser
    void getUserInfoDocsTest() throws Exception {
        // given
        User user = createUser();
        UserInfoResponseDto result = createUserInfoResponseDto(user);
        when(userService.getUserInfo(any(User.class)))
                .thenReturn(result);

        // when &  then
        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "access token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-info-get",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("API 인증 Access Token")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("result.age").type(JsonFieldType.NUMBER).description("유저 나이"),
                                fieldWithPath("result.area").type(JsonFieldType.STRING).description(generateLinkCode(AREA_CODE)),
                                fieldWithPath("result.city").type(JsonFieldType.STRING).description(generateLinkCode(CITY_CODE)),
                                fieldWithPath("result.lastYearIncome").type(JsonFieldType.NUMBER).description("유저 작년 소득"),
                                fieldWithPath("result.educationType").type(JsonFieldType.STRING).description(generateLinkCode(EDUCATION_TYPE)),
                                fieldWithPath("result.jobs").type(JsonFieldType.ARRAY).description(generateLinkCode(JOB_TYPE)),
                                fieldWithPath("result.gender").type(JsonFieldType.STRING).description(generateLinkCode(GENDER)),
                                fieldWithPath("result.smallBusiness").type(JsonFieldType.BOOLEAN).description("중소기업 여부"),
                                fieldWithPath("result.soldier").type(JsonFieldType.BOOLEAN).description("군인 여부"),
                                fieldWithPath("result.lowIncome").type(JsonFieldType.BOOLEAN).description("저소득층 여부"),
                                fieldWithPath("result.disabled").type(JsonFieldType.BOOLEAN).description("장애인 여부"),
                                fieldWithPath("result.localTalent").type(JsonFieldType.BOOLEAN).description("지역인재 여부"),
                                fieldWithPath("result.farmer").type(JsonFieldType.BOOLEAN).description("농업 종사자 여부")
                        )
                ));

    }

    @DisplayName("홈 정보 조회 API")
    @Test
    @WithMockUser
    void getHomeInfoDocsTest() throws Exception {
        // given
        User user = createUser();
        HomeInfoResponseDto result = createHomeInfoResponseDto(user);

        when(userService.getHomeInfo(any(User.class)))
                .thenReturn(result);

        // when &  then
        mockMvc.perform(get("/user/home")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "access token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-home-get",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("API 인증 Access Token")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("result.characterImage").type(JsonFieldType.STRING).description("유저 캐릭터 이미지 url"),
                                fieldWithPath("result.characterNickname").type(JsonFieldType.STRING).description("유저 캐릭터 닉네임"),
                                fieldWithPath("result.characterPercent").type(JsonFieldType.NUMBER).description("유저 캐릭터 상위 퍼센트"),
                                fieldWithPath("result.description").type(JsonFieldType.STRING).description("유저 설명 텍스트"),
                                fieldWithPath("result.interestPolicyCnt").type(JsonFieldType.NUMBER).description("유저의 관심 정책 수"),
                                fieldWithPath("result.applyPolicyCnt").type(JsonFieldType.NUMBER).description("유저의 수혜(신청) 정책 수"),
                                fieldWithPath("result.recommendPolicy").type(JsonFieldType.ARRAY).description("추천 정책"),
                                fieldWithPath("result.recommendPolicy[].policyId").type(JsonFieldType.NUMBER).description("정책 ID"),
                                fieldWithPath("result.recommendPolicy[].policyName").type(JsonFieldType.STRING).description("정책 이름"),
                                fieldWithPath("result.recommendPolicy[].policyLogo").type(JsonFieldType.STRING).description("정책 로고"),
                                fieldWithPath("result.recommendPolicy[].supportPolicyType").type(JsonFieldType.STRING).description(generateLinkCode(SUPPORT_POLICY_TYPE)),
                                fieldWithPath("result.recommendPolicy[].supportPolicyTypeName").type(JsonFieldType.STRING).description("지원 정책 유형 이름"),
                                fieldWithPath("result.recommendPolicy[].dueDate").type(JsonFieldType.NUMBER).description("디데이"),
                                fieldWithPath("result.endDatePolicy").type(JsonFieldType.ARRAY).description("신청 마감일 기준으로 한 정책"),
                                fieldWithPath("result.endDatePolicy[].policyId").type(JsonFieldType.NUMBER).description("정책 ID"),
                                fieldWithPath("result.endDatePolicy[].policyName").type(JsonFieldType.STRING).description("정책 이름"),
                                fieldWithPath("result.endDatePolicy[].policyLogo").type(JsonFieldType.STRING).description("정책 로고"),
                                fieldWithPath("result.endDatePolicy[].supportPolicyType").type(JsonFieldType.STRING).description(generateLinkCode(SUPPORT_POLICY_TYPE)),
                                fieldWithPath("result.endDatePolicy[].supportPolicyTypeName").type(JsonFieldType.STRING).description("지원 정책 유형 이름"),
                                fieldWithPath("result.endDatePolicy[].dueDate").type(JsonFieldType.NUMBER).description("디데이")
                        )
                ));
    }

    @DisplayName("fcm 토큰 업데이트 API 테스트")
    @Test
    @WithMockUser
    void updateFcmTokenDocsTest() throws Exception {
        // given
        String fcmToken = "testFcmToken";

        // when & then
        mockMvc.perform(patch("/user/fcm_token")
                        .param("fcmToken", fcmToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "access token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-fcmtoken-patch",
                        preprocessResponse(prettyPrint()),
                        requestParameters(parameterWithName("fcmToken").description("FCM 토큰")),
                        requestHeaders(headerWithName("Authorization").description("API 인증 Access Token")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("응답 결과")
                        )
                ));
    }

    @DisplayName("알람 수신 여부 업데이트 API 테스트")
    @Test
    @WithMockUser
    void updatePushNotificationStatusDocsTest() throws Exception {
        // given
        String pushNotificationStatus = "true";

        // when & then
        mockMvc.perform(patch("/user/notification")
                        .param("pushNotificationStatus", pushNotificationStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "access token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-notification-patch",
                        preprocessResponse(prettyPrint()),
                        requestParameters(parameterWithName("pushNotificationStatus").description("알림 상태 (true 또는 false)")),
                        requestHeaders(headerWithName("Authorization").description("API 인증 Access Token")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("응답 결과")
                        )
                ));
    }

    @DisplayName("알람 수신 여부 조회 API 테스트")
    @Test
    @WithMockUser
    void getAlarmStatusDocsTest() throws Exception {
        // given
        when(userService.getAlarm(any(User.class)))
                .thenReturn(UserAlarmStatusResponseDto.builder().alarmStatus(true).build());

        // when & then
        mockMvc.perform(get("/user/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "access token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-notification-get",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("API 인증 Access Token")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.alarmStatus").type(JsonFieldType.BOOLEAN).description("유저 알람 수신 여부")
                        )
                ));
    }

    @DisplayName("메뉴얼 상태 업데이트 API 테스트")
    @Test
    @WithMockUser
    void updateManualStatusDocsTest() throws Exception {

        // when & then
        mockMvc.perform(patch("/user/manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "access token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-manual-patch",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("API 인증 Access Token")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("응답 결과")
                        )
                ));
    }

    @DisplayName("메뉴얼 상태 조회 API 테스트")
    @Test
    @WithMockUser
    void getManualStatusDocsTest() throws Exception {
        // given

        // when & then
        mockMvc.perform(get("/user/manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "access token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-manual-get",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("API 인증 Access Token")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.manualStatus").type(JsonFieldType.BOOLEAN).description("메뉴얼 상태")
                        )
                ));
    }

    @DisplayName("이용약관 조회 API 테스트")
    @Test
    @WithMockUser
    void getPrivacyDocsTest() throws Exception {

        PrivacyInfoResponseDto result = PrivacyInfoResponseDto.builder()
                .termsOfServiceDate(LocalDate.now())
                .privacyDate(LocalDate.now())
                .marketingDate(LocalDate.now())
                .termsOfServiceUrl("이용약관 url")
                .privacyUrl("개인정보처리방침 url")
                .marketingUrl("마켓팅 동의 url")
                .build();

        when(userService.getPrivacyInfo(any(User.class)))
                .thenReturn(result);

        // when & then
        mockMvc.perform(get("/user/privacy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "access token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-privacy-get",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("API 인증 Access Token")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.termsOfServiceDate").type(JsonFieldType.STRING).description("이용약관 날짜"),
                                fieldWithPath("result.privacyDate").type(JsonFieldType.STRING).description("개인정보처리방침 날짜"),
                                fieldWithPath("result.marketingDate").type(JsonFieldType.STRING).description("마켓팅 동의 날짜"),
                                fieldWithPath("result.termsOfServiceUrl").type(JsonFieldType.STRING).description("이용약관 url"),
                                fieldWithPath("result.privacyUrl").type(JsonFieldType.STRING).description("개인정보처리방침 url"),
                                fieldWithPath("result.marketingUrl").type(JsonFieldType.STRING).description("마켓팅 동의 url")
                        )
                ));
    }


    private UserInfoResponseDto createUserInfoResponseDto(User user) {
        return UserInfoResponseDto.builder()
                .nickname(user.getNickname())
                .age(user.getAge())
                .area(user.getAddress().getAreaCode().getName())
                .city(user.getAddress().getCityCode().getName())
                .lastYearIncome(user.getLastYearIncome())
                .educationType(user.getEducationType().getDescription())
                .jobs(user.getJobs().stream().map(jobType -> jobType.getDescription()).collect(Collectors.toSet()))
                .gender(user.getUserDetail().getGender().getDescription())
                .smallBusiness(user.getUserDetail().isSmallBusiness())
                .soldier(user.getUserDetail().isSoldier())
                .lowIncome(user.getUserDetail().isLowIncome())
                .disabled(user.getUserDetail().isDisabled())
                .localTalent(user.getUserDetail().isLocalTalent())
                .farmer(user.getUserDetail().isFarmer())
                .build();
    }

    private HomeInfoResponseDto createHomeInfoResponseDto(User user) {
        return HomeInfoResponseDto.builder()
                .nickname(user.getNickname())
                .characterImage("testCharacterImage")
                .characterNickname(Character.NEW.getName())
                .characterPercent(10)
                .description(Character.NEW.getDescription())
                .interestPolicyCnt(5)
                .applyPolicyCnt(6)
                .recommendPolicy(List.of(createHomePolicyInfo(1L, SupportPolicyType.MONEY), createHomePolicyInfo(2L, SupportPolicyType.LOANS), createHomePolicyInfo(3L, SupportPolicyType.SOCIAL_SERVICE)))
                .endDatePolicy(List.of(createHomePolicyInfo(4L, SupportPolicyType.MONEY), createHomePolicyInfo(5L, SupportPolicyType.LOANS), createHomePolicyInfo(6L, SupportPolicyType.SOCIAL_SERVICE)))
                .build();
    }

    private HomeInfoResponseDto.HomePolicyInfo createHomePolicyInfo(Long policyId, SupportPolicyType supportPolicyType) {
        return HomeInfoResponseDto.HomePolicyInfo.builder()
                .policyId(policyId)
                .policyName("testPolicyName")
                .policyLogo("testPolicyLogo")
                .supportPolicyType(supportPolicyType)
                .supportPolicyTypeName(supportPolicyType.getDescription())
                .dueDate(10)
                .build();
    }

    private Policy createPolicy() {
        return Policy.builder()
                .bizId("BIZ-12345") // 정책 ID
                .policyName("청년 취업 지원 정책") // 정책명
                .policyIntroduction("청년 취업을 지원하는 정책입니다.") // 정책 소개
                .operatingAgencyName("고용노동부") // 운영 기관명
                .applicationPeriodContent("1월 1일부터 12월 31일까지") // 사업 기간 신청 내용
                .organizationType("정부") // 기관 및 지자체 구분
                .supportContent("재정 지원 및 직업 훈련 제공") // 지원 내용
                .ageInfo("18-35세") // 참여 요건 - 나이
                .employmentStatusContent("미취업 또는 불완전 고용 상태") // 취업 상태 내용
                .specializedFieldContent("IT, 공학") // 특화 분야 내용
                .educationalRequirementContent("학사 이상 학력 소지자") // 학력 요건 내용
                .residentialAndIncomeRequirementContent("서울 거주, 연 소득 3000만원 이하") // 거주지 및 소득 조건 내용
                .additionalClauseContent("범죄 기록 없음") // 추가 단서 사항 내용
                .eligibilityTargetContent("범죄 기록 없는 청년") // 참여 제한 대상 내용
                .duplicatePolicyCode("DUP-001") // 중복 불가 정책 코드
                .applicationSiteAddress("https://application.site") // 신청 사이트 주소
                .referenceSiteUrlAddress("https://reference.site") // 참고 사이트 URL 주소
                .applicationProcedureContent("온라인 신청서 제출") // 신청 절차 내용
                .submissionDocumentContent("신분증, 학력 증명서, 소득 증빙 자료") // 제출 서류 내용
                .minAge(18) // 최소 나이
                .maxAge(35) // 최대 나이
                .areaCode(AreaCode.SEOUL) // 지역 코드 - 시, 도
                .cityCode(CityCode.GANGNAM) // 지역 코드 - 구
                .jobTypes(Set.of(JobType.UNEMPLOYED, JobType.ENTREPRENEUR)) // 직업 유형
                .educationTypes(Set.of(EducationType.COLLEGE_GRADUATE, EducationType.COLLEGE_STUDENT)) // 학력 유형
                .policySplzTypes(Set.of(PolicySplzType.UNLIMITED, PolicySplzType.ENTREPRENEUR)) // 특화 분야 유형
                .policyCode(PolicyCode.RESIDENCE) // 정책 유형
                .policyLogo("https://logo.url") // 정책 로고
                .policyApplyDenialReason("자격 요건 미충족") // 신청 불가 사유
                .applyStatus("열림") // 신청 가능 상태
                .applySttDate(LocalDate.of(2023, 1, 1)) // 신청 시작일
                .applyEndDate(LocalDate.of(2023, 12, 31)) // 신청 종료일
//                .userPolicies(new HashSet<>()) // 사용자 정책 (현재는 없음)
//                .benefit(BigDecimal.valueOf(100000)) // 수혜 금액
//                .remark("추가 비고 사항") // 비고
//                .applyPeriods(List.of(
//                        new ApplyPeriod(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 3, 31)),
//                        new ApplyPeriod(LocalDate.of(2023, 4, 1), LocalDate.of(2023, 6, 30))
//                )) // 신청 기간
//                .policyDateType(PolicyDateType.FIXED) // 정책 날짜 유형
                .build();
    }

    private static User createUser() {
        return User.builder()
                .email("test@example.com")
                .nickname("test")
                .age(30)
                .lastYearIncome(50000.0)
                .address(Address.builder()
                        .areaCode(AreaCode.SEOUL)
                        .cityCode(CityCode.GANGSEO)
                        .build())
                .educationType(EducationType.COLLEGE_GRADUATE)
                .jobs(Set.of(JobType.EMPLOYED, JobType.ENTREPRENEUR))
                .policyCnt(5)
                .fcmToken("fcm_token")
                .pushNotificationStatus(true)
                .appNotificationStatus(true)
                .provider(ProviderType.KAKAO)
                .benefit(1000)
                .userDetail(UserDetail.builder()
                        .gender(Gender.MALE)
                        .disabled(true)
                        .farmer(true)
                        .localTalent(true)
                        .lowIncome(true)
                        .smallBusiness(true)
                        .soldier(true)
                        .disabled(true)
                        .build())
                .build();
    }


    private static ModifyRequestDto createModifyRequestDto() {
        return ModifyRequestDto.builder()
                .nickname("paul")
                .age(25)
                .areaCode(AreaCode.SEOUL)
                .cityCode(CityCode.GANGSEO)
                .lastYearIncome(Double.valueOf(50000000))
                .educationType(EducationType.COLLEGE_STUDENT)
                .jobs(Set.of(JobType.EMPLOYED))
                .userDetail(UserDetail.builder()
                        .gender(Gender.MALE)
                        .disabled(true)
                        .farmer(true)
                        .localTalent(true)
                        .lowIncome(true)
                        .smallBusiness(true)
                        .soldier(true)
                        .disabled(true)
                        .build())
                .build();
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