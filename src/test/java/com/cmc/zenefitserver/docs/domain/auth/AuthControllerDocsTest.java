package com.cmc.zenefitserver.docs.domain.auth;

import com.cmc.zenefitserver.docs.RestDocsSupport;
import com.cmc.zenefitserver.global.auth.AuthController;
import com.cmc.zenefitserver.global.auth.AuthService;
import com.cmc.zenefitserver.global.auth.ProviderType;
import com.cmc.zenefitserver.global.common.request.AuthRequestDto;
import com.cmc.zenefitserver.global.common.response.TokenResponseDto;
import com.cmc.zenefitserver.global.config.WebMvcConfig;
import com.cmc.zenefitserver.global.error.GlobalExceptionHandler;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import com.cmc.zenefitserver.global.error.exception.NoAuthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.cmc.zenefitserver.docs.utils.DocumentLinkGenerator.DocUrl.PROVIDER;
import static com.cmc.zenefitserver.docs.utils.DocumentLinkGenerator.generateLinkCode;
import static com.cmc.zenefitserver.global.error.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(WebMvcConfig.class)
class AuthControllerDocsTest extends RestDocsSupport {

    private static final AuthService authServie = mock(AuthService.class);

    @Override
    protected Object initController() {
        return new AuthController(authServie);
    }

    @DisplayName("기존 유저 로그인 API 테스트")
    @Test
    void loginSuccessTest() throws Exception {
        // given
        AuthRequestDto request = createAuthRequestDto(ProviderType.KAKAO);

        TokenResponseDto response = createTokenResponseDto();

        when(authServie.login(any(AuthRequestDto.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("auth-login-post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("providerType").type(JsonFieldType.STRING).description(generateLinkCode(PROVIDER)),
                                fieldWithPath("token").type(JsonFieldType.STRING).description("소셜 토큰 (kakao는 code)"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("소셜 닉네임 (애플 첫 로그인 시에만 넣어주시면 됩니다.)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.accessToken").type(JsonFieldType.STRING).description("액세스토큰"),
                                fieldWithPath("result.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                                fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("유저 닉네임")
                        )
                ));
    }

    @DisplayName("새로운 유저 로그인 API 테스트")
    @Test
    void loginWithNewUserTest() throws Exception {
        // given
        AuthRequestDto request = createAuthRequestDto(ProviderType.KAKAO);

        Long userId = 3001L;

        when(authServie.login(any(AuthRequestDto.class)))
                .thenThrow(new NoAuthException(NOT_FOUND_USER, Map.of("userId", "3001")));
        // when & then
        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andDo(document("auth-login-new-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("providerType").type(JsonFieldType.STRING).description(generateLinkCode(PROVIDER)),
                                fieldWithPath("token").type(JsonFieldType.STRING).description("소셜 토큰 (kakao는 code)"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("소셜 닉네임 (애플 첫 로그인 시에만 넣어주시면 됩니다.)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.userId").type(JsonFieldType.STRING).description("유저 ID")
                        )
                ));
    }

    @DisplayName("임시 회원가입 유저 로그인 API 테스트")
    @Test
    void loginWithTemporaryUserTest() throws Exception {
        // given
        AuthRequestDto request = createAuthRequestDto(ProviderType.KAKAO);

        Long userId = 3001L;

        when(authServie.login(any(AuthRequestDto.class)))
                .thenThrow(new NoAuthException(INVALID_USER, Map.of("userId", "3001")));
        // when & then
        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andDo(document("auth-login-temporary-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("providerType").type(JsonFieldType.STRING).description(generateLinkCode(PROVIDER)),
                                fieldWithPath("token").type(JsonFieldType.STRING).description("소셜 토큰 (kakao는 code)"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("소셜 닉네임 (애플 첫 로그인 시에만 넣어주시면 됩니다.)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.userId").type(JsonFieldType.STRING).description("유저 ID")
                        )
                ));
    }

    private AuthRequestDto createAuthRequestDto(ProviderType providerType) {
        return AuthRequestDto.builder()
                .providerType(providerType)
                .token("p1b3M_ikmtHivvFJqwY5bXAYg-ilCq4E7DnJwlT5CisM0wAAAYOPy1oR")
                .nickname("nickname")
                .build();
    }

    private TokenResponseDto createTokenResponseDto() {
        return TokenResponseDto.builder()
                .accessToken("access token")
                .refreshToken("refresh token")
                .nickname("유저 닉네임 test")
                .build();
    }


}