package com.cmc.zenefitserver.docs.domain.userpolicy;


import com.cmc.zenefitserver.docs.RestDocsSupport;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.userpolicy.api.UserPolicyController;
import com.cmc.zenefitserver.domain.userpolicy.application.UserPolicyService;
import com.cmc.zenefitserver.domain.userpolicy.dto.response.ApplyPolicyListResponseDto;
import com.cmc.zenefitserver.domain.userpolicy.dto.response.InterestPolicyListResponseDto;
import com.cmc.zenefitserver.domain.userpolicy.dto.response.PolicySizeResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserPolicyControllerDocsTest extends RestDocsSupport {

    private final UserPolicyService userPolicyService = mock(UserPolicyService.class);

    @Override
    protected Object initController() {
        return new UserPolicyController(userPolicyService);
    }

    @DisplayName("관심 정책 저장 API 테스트")
    @Test
    @WithMockUser
    void saveInterestPolicyTest() throws Exception {
        // given
        Long policyId = 1L;

        // when & then
        mockMvc.perform(post("/user/policy/{policyId}", policyId)
                        .header("Authentication", "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-policy-interest-post",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("policyId").description("정책 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("응답 결과")
                        )
                ));
    }

    @DisplayName("수혜 정책 저장 API 테스트")
    @Test
    @WithMockUser
    void saveApplyPolicy() throws Exception {
        // given
        Long policyId = 1L;

        // when & then
        mockMvc.perform(post("/user/policy/apply/{policyId}", policyId)
                        .header("Authentication", "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-policy-apply-post",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("policyId").description("정책 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("응답 결과")
                        )
                ));
    }

    @DisplayName("관심 정책 리스트 조회 API")
    @Test
    @WithMockUser
    void getInterestPolicesByPaging() throws Exception {
        // given

        Page<InterestPolicyListResponseDto> response = createInterestPolicyResponseDtoPage();

        when(userPolicyService.getUserPoliciesByInterestFlag(any(User.class), any(Boolean.class), any(Integer.class), any(Integer.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/user/policy")
                        .header("Authentication", "Access Token")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-policy-interest-get",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("page").description("조회할 페이지 번호").optional(),
                                parameterWithName("size").description("페이지당 레코드 수").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.content[].policyId").type(JsonFieldType.NUMBER).description("정책 ID"),
                                fieldWithPath("result.content[].policyName").type(JsonFieldType.STRING).description("정책 이름"),
                                fieldWithPath("result.content[].policyIntroduction").type(JsonFieldType.STRING).description("정책 소개"),
                                fieldWithPath("result.content[].policyLogo").type(JsonFieldType.STRING).description("정책 로고"),
                                fieldWithPath("result.content[].applyEndDate").type(JsonFieldType.STRING).description("정책 신청 마감일"),
                                fieldWithPath("result.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                                fieldWithPath("result.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                fieldWithPath("result.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("result.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("result.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("비어 있는 정렬 정보 여부"),
                                fieldWithPath("result.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                                fieldWithPath("result.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("result.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("result.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이지 여부"),
                                fieldWithPath("result.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이지 여부"),
                                fieldWithPath("result.totalElements").type(JsonFieldType.NUMBER).description("총 요소 수"),
                                fieldWithPath("result.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("result.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("result.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("result.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("result.sort").type(JsonFieldType.OBJECT).description("현재 정렬 정보"),
                                fieldWithPath("result.sort.sorted").type(JsonFieldType.BOOLEAN).description("현재 정렬 여부"),
                                fieldWithPath("result.sort.unsorted").type(JsonFieldType.BOOLEAN).description("현재 비정렬 여부"),
                                fieldWithPath("result.sort.empty").type(JsonFieldType.BOOLEAN).description("현재 비어 있는 정렬 정보 여부"),
                                fieldWithPath("result.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                                fieldWithPath("result.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("result.empty").type(JsonFieldType.BOOLEAN).description("비어 있는 페이지 여부")
                        )
                ));

    }

    @DisplayName("수혜(신청) 정책 리스트 조회 API")
    @Test
    @WithMockUser
    void getApplyPolicesByPaging() throws Exception {
        // given

        Page<ApplyPolicyListResponseDto> response = createIcreateApplyPolicyListResponseDtoPage();

        when(userPolicyService.getUserPoliciesByApplyFlag(any(User.class), any(Boolean.class), any(Integer.class), any(Integer.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/user/policy/apply")
                        .header("Authentication", "Access Token")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-policy-apply-get",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("page").description("조회할 페이지 번호").optional(),
                                parameterWithName("size").description("페이지당 레코드 수").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.content[].policyId").type(JsonFieldType.NUMBER).description("정책 ID"),
                                fieldWithPath("result.content[].policyName").type(JsonFieldType.STRING).description("정책 이름"),
                                fieldWithPath("result.content[].policyIntroduction").type(JsonFieldType.STRING).description("정책 소개"),
                                fieldWithPath("result.content[].policyLogo").type(JsonFieldType.STRING).description("정책 로고"),
                                fieldWithPath("result.content[].benefit").type(JsonFieldType.NUMBER).description("정책 수혜 금액"),
                                fieldWithPath("result.content[].benefitPeriod").type(JsonFieldType.STRING).description("수혜 금액 기간 단위 ( 년 또는 월 )"),
                                fieldWithPath("result.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                                fieldWithPath("result.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                fieldWithPath("result.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("result.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("result.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("비어 있는 정렬 정보 여부"),
                                fieldWithPath("result.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                                fieldWithPath("result.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("result.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("result.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이지 여부"),
                                fieldWithPath("result.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이지 여부"),
                                fieldWithPath("result.totalElements").type(JsonFieldType.NUMBER).description("총 요소 수"),
                                fieldWithPath("result.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("result.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("result.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("result.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("result.sort").type(JsonFieldType.OBJECT).description("현재 정렬 정보"),
                                fieldWithPath("result.sort.sorted").type(JsonFieldType.BOOLEAN).description("현재 정렬 여부"),
                                fieldWithPath("result.sort.unsorted").type(JsonFieldType.BOOLEAN).description("현재 비정렬 여부"),
                                fieldWithPath("result.sort.empty").type(JsonFieldType.BOOLEAN).description("현재 비어 있는 정렬 정보 여부"),
                                fieldWithPath("result.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                                fieldWithPath("result.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("result.empty").type(JsonFieldType.BOOLEAN).description("비어 있는 페이지 여부")
                        )
                ));
    }

    @DisplayName("관심 정책 삭제 API")
    @Test
    @WithMockUser
    void deleteInterestPolicy() throws Exception {

        // given
        Long policyId = 1L;

        // when & then
        mockMvc.perform(delete("/user/policy/{policyId}", policyId)
                        .header("Authentication", "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-policy-interest-delete",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("policyId").description("정책 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("응답 결과")
                        )
                ));
    }

    @DisplayName("수혜(신청) 정책 삭제 API")
    @Test
    @WithMockUser
    void deleteApplyPolicy() throws Exception {
        // given
        Long policyId = 1L;

        // when & then
        mockMvc.perform(delete("/user/policy/apply/{policyId}", policyId)
                        .header("Authentication", "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-policy-apply-delete",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("policyId").description("정책 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("응답 결과")
                        )
                ));
    }

    @DisplayName("총 관심 정책 삭제 API")
    @Test
    @WithMockUser
    void deleteAllInterestPolicy() throws Exception {
        // given
        Long policyId = 1L;

        // when & then
        mockMvc.perform(delete("/user/policy/all")
                        .header("Authentication", "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-policy-interest-all-delete",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("응답 결과")
                        )
                ));
    }

    @DisplayName("총 수혜(신청) 정책 삭제 API")
    @Test
    @WithMockUser
    void deleteAllApplyPolicy() throws Exception {
        mockMvc.perform(delete("/user/policy/apply/all")
                        .header("Authentication", "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-policy-apply-all-delete",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.NULL).description("응답 결과")
                        )
                ));
    }

    @DisplayName("총 관심 정책의 크기 조회 API")
    @Test
    @WithMockUser
    void getAllInterestPolicySize() throws Exception {
        // given
        PolicySizeResponseDto response = PolicySizeResponseDto.builder()
                .size(10)
                .build();

        when(userPolicyService.getAllInterestPolicySize(any(User.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/user/policy/size")
                        .header("Authentication", "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-policy-interest-size-get",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.size").type(JsonFieldType.NUMBER).description("정책 수")
                        )
                ));
    }

    @DisplayName("총 수혜(신청) 정책 크기 조회 API")
    @Test
    @WithMockUser
    void getAllApplyPolicySize() throws Exception{
        // given
        PolicySizeResponseDto response = PolicySizeResponseDto.builder()
                .size(10)
                .build();

        when(userPolicyService.getAllApplyPolicySize(any(User.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/user/policy/apply/size")
                        .header("Authentication", "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-policy-apply-size-get",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.size").type(JsonFieldType.NUMBER).description("정책 수")
                        )
                ));
    }

    private Page<InterestPolicyListResponseDto> createInterestPolicyResponseDtoPage() {
        List<InterestPolicyListResponseDto> interestPolicyResponseDtoList = List.of(createInterestPolicyResponseDto(1L), createInterestPolicyResponseDto(2L));
        return new PageImpl<>(interestPolicyResponseDtoList, PageRequest.of(0, 10), interestPolicyResponseDtoList.size());
    }

    private InterestPolicyListResponseDto createInterestPolicyResponseDto(Long policyId) {
        return InterestPolicyListResponseDto.builder()
                .policyId(policyId)
                .policyName("청년도약계좌")
                .policyIntroduction("월 70만원을 5년 납입하면 약 5,000만원을 적립할 수 있는 청년도약계좌")
                .policyLogo("기관 로고 url")
                .applyEndDate(LocalDate.of(2024, 12, 31))
                .build();
    }

    private Page<ApplyPolicyListResponseDto> createIcreateApplyPolicyListResponseDtoPage() {
        List<ApplyPolicyListResponseDto> interestPolicyResponseDtoList = List.of(createApplyPolicyListResponseDto(1L), createApplyPolicyListResponseDto(2L));
        return new PageImpl<>(interestPolicyResponseDtoList, PageRequest.of(0, 10), interestPolicyResponseDtoList.size());
    }

    private ApplyPolicyListResponseDto createApplyPolicyListResponseDto(Long policyId) {
        return ApplyPolicyListResponseDto.builder()
                .policyId(policyId)
                .policyName("청년도약계좌")
                .policyIntroduction("월 70만원을 5년 납입하면 약 5,000만원을 적립할 수 있는 청년도약계좌")
                .policyLogo("기관 로고 url")
                .benefit(BigDecimal.valueOf(1000000))
                .benefitPeriod("월")
                .build();
    }


}
