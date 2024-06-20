package com.cmc.zenefitserver.docs.domain.user.api;

import com.cmc.zenefitserver.docs.RestDocsSupport;
import com.cmc.zenefitserver.domain.policy.api.PolicyController;
import com.cmc.zenefitserver.domain.policy.application.PolicyService;
import com.cmc.zenefitserver.domain.policy.domain.enums.*;
import com.cmc.zenefitserver.domain.policy.dto.request.PolicyListRequestDto;
import com.cmc.zenefitserver.domain.policy.dto.request.SearchPolicyListRequestDto;
import com.cmc.zenefitserver.domain.policy.dto.response.*;
import com.cmc.zenefitserver.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.cmc.zenefitserver.docs.utils.DocumentLinkGenerator.DocUrl.*;
import static com.cmc.zenefitserver.docs.utils.DocumentLinkGenerator.DocUrl.JOB_TYPE;
import static com.cmc.zenefitserver.docs.utils.DocumentLinkGenerator.generateLinkCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

public class PolicyControllerDocsTest extends RestDocsSupport {

    private final PolicyService policyService = mock(PolicyService.class);

    @Override
    protected Object initController() {
        return new PolicyController(policyService);
    }

    @DisplayName("정책 목록 조회 API 테스트")
    @Test
    @WithMockUser
    void getPolicesTest() throws Exception {
        // given
        PolicyListRequestDto request = PolicyListRequestDto.builder()
                .supportPolicyType(SupportPolicyType.MONEY)
                .policyType(PolicyCode.JOB)
                .build();

        Page<PolicyListInfoDto> response = createPolicyListInfoDtoPage();

        when(policyService.getPolicyList(any(User.class), any(PolicyListRequestDto.class), any(Integer.class), any(Integer.class), any(Sort.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/policy")
                        .content(objectMapper.writeValueAsString(request))
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortField", "applyEndDate")
                        .param("sortOrder", "asc")
                        .header("Authorization", "access token")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("polices-post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("page").description("조회할 페이지 번호").optional(),
                                parameterWithName("size").description("페이지당 레코드 수").optional(),
                                parameterWithName("sortField").description("정렬할 필드 (applySttDate 또는 applyEndDate)").optional(),
                                parameterWithName("sortOrder").description("정렬 순서 (asc 또는 desc)").optional()
                        ),
                        requestFields(
                                fieldWithPath("supportPolicyType").type(JsonFieldType.STRING).description(generateLinkCode(SUPPORT_POLICY_TYPE)),
                                fieldWithPath("policyType").type(JsonFieldType.STRING).description(generateLinkCode(POLICY_TYPE))
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.content[].policyId").type(JsonFieldType.NUMBER).description("정책 ID"),
                                fieldWithPath("result.content[].policyName").type(JsonFieldType.STRING).description("정책 이름"),
                                fieldWithPath("result.content[].policyLogo").type(JsonFieldType.STRING).description("정책 로고"),
                                fieldWithPath("result.content[].policyIntroduction").type(JsonFieldType.STRING).description("정책 소개"),
                                fieldWithPath("result.content[].areaCode").type(JsonFieldType.STRING).description(generateLinkCode(AREA_CODE)),
                                fieldWithPath("result.content[].cityCode").type(JsonFieldType.STRING).description(generateLinkCode(CITY_CODE)),
                                fieldWithPath("result.content[].policyDateType").type(JsonFieldType.STRING).description(generateLinkCode(POLICY_DATE_TYPE)),
                                fieldWithPath("result.content[].policyDateTypeDescription").type(JsonFieldType.STRING).description("정책 날짜 설명"),
                                fieldWithPath("result.content[].policyApplyDenialReason").type(JsonFieldType.STRING).description("정책 불가 사유"),
                                fieldWithPath("result.content[].applyStatus").type(JsonFieldType.BOOLEAN).description("신청 가능 상태"),
                                fieldWithPath("result.content[].benefit").type(JsonFieldType.NUMBER).description("수혜 금액"),
                                fieldWithPath("result.content[].benefitPeriod").type(JsonFieldType.STRING).description("수혜 금액 기간"),
                                fieldWithPath("result.content[].applyFlag").type(JsonFieldType.BOOLEAN).description("수혜(신청) 정책 여부"),
                                fieldWithPath("result.content[].interestFlag").type(JsonFieldType.BOOLEAN).description("관심 정책 여부"),
                                fieldWithPath("result.content[].policyMethodType").type(JsonFieldType.STRING).description(generateLinkCode(POLICY_METHOD_TYPE)),
                                fieldWithPath("result.content[].policyMethodTypeDescription").type(JsonFieldType.STRING).description("정책 신청 방법 설명"),
                                fieldWithPath("result.content[].policyUrl").type(JsonFieldType.STRING).description("정책 신청 URL"),
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

    @DisplayName("정책 검색 API 테스트")
    @Test
    @WithMockUser
    void getSearchPolicesTest() throws Exception {
        // given
        SearchPolicyListRequestDto request = SearchPolicyListRequestDto.builder()
                .supportPolicyType(SupportPolicyType.MONEY)
                .policyType(PolicyCode.NONE)
                .keyword("청년")
                .build();

        PolicyListResponseDto response = creatPolicyListResponseDto();

        when(policyService.getSearchPolicyList(any(User.class), any(SearchPolicyListRequestDto.class), any(Integer.class), any(Integer.class), any(Sort.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/policy/search")
                        .content(objectMapper.writeValueAsString(request))
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortField", "applyEndDate")
                        .param("sortOrder", "asc")
                        .header("Authorization", "access token")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("policy-search-post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("page").description("조회할 페이지 번호").optional(),
                                parameterWithName("size").description("페이지당 레코드 수").optional(),
                                parameterWithName("sortField").description("정렬할 필드").optional(),
                                parameterWithName("sortOrder").description("정렬 순서 (asc 또는 desc)").optional()
                        ),
                        requestFields(
                                fieldWithPath("keyword").type(JsonFieldType.STRING).description("키워드"),
                                fieldWithPath("supportPolicyType").type(JsonFieldType.STRING).description(generateLinkCode(SUPPORT_POLICY_TYPE)),
                                fieldWithPath("policyType").type(JsonFieldType.STRING).description(generateLinkCode(POLICY_TYPE))
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].policyId").type(JsonFieldType.NUMBER).description("정책 ID"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].policyName").type(JsonFieldType.STRING).description("정책 이름"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].policyLogo").type(JsonFieldType.STRING).description("정책 로고"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].policyIntroduction").type(JsonFieldType.STRING).description("정책 소개"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].areaCode").type(JsonFieldType.STRING).description(generateLinkCode(AREA_CODE)),
                                fieldWithPath("result.policyListInfoResponseDto.content[].cityCode").type(JsonFieldType.STRING).description(generateLinkCode(CITY_CODE)),
                                fieldWithPath("result.policyListInfoResponseDto.content[].policyDateType").type(JsonFieldType.STRING).description(generateLinkCode(POLICY_DATE_TYPE)),
                                fieldWithPath("result.policyListInfoResponseDto.content[].policyDateTypeDescription").type(JsonFieldType.STRING).description("정책 날짜 설명"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].policyApplyDenialReason").type(JsonFieldType.STRING).description("정책 불가 사유"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].applyStatus").type(JsonFieldType.BOOLEAN).description("신청 가능 상태"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].benefit").type(JsonFieldType.NUMBER).description("수혜 금액"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].benefitPeriod").type(JsonFieldType.STRING).description("수혜 금액 기간"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].applyFlag").type(JsonFieldType.BOOLEAN).description("수혜(신청) 정책 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].interestFlag").type(JsonFieldType.BOOLEAN).description("관심 정책 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].policyMethodType").type(JsonFieldType.STRING).description(generateLinkCode(POLICY_METHOD_TYPE)),
                                fieldWithPath("result.policyListInfoResponseDto.content[].policyMethodTypeDescription").type(JsonFieldType.STRING).description("정책 신청 방법 설명"),
                                fieldWithPath("result.policyListInfoResponseDto.content[].policyUrl").type(JsonFieldType.STRING).description("정책 신청 URL"),
                                fieldWithPath("result.policyListInfoResponseDto.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                                fieldWithPath("result.policyListInfoResponseDto.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                fieldWithPath("result.policyListInfoResponseDto.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("비어 있는 정렬 정보 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                                fieldWithPath("result.policyListInfoResponseDto.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("result.policyListInfoResponseDto.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("result.policyListInfoResponseDto.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이지 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이지 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.totalElements").type(JsonFieldType.NUMBER).description("총 요소 수"),
                                fieldWithPath("result.policyListInfoResponseDto.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("result.policyListInfoResponseDto.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("result.policyListInfoResponseDto.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("result.policyListInfoResponseDto.sort").type(JsonFieldType.OBJECT).description("현재 정렬 정보"),
                                fieldWithPath("result.policyListInfoResponseDto.sort.sorted").type(JsonFieldType.BOOLEAN).description("현재 정렬 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.sort.unsorted").type(JsonFieldType.BOOLEAN).description("현재 비정렬 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.sort.empty").type(JsonFieldType.BOOLEAN).description("현재 비어 있는 정렬 정보 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                                fieldWithPath("result.policyListInfoResponseDto.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("result.policyListInfoResponseDto.empty").type(JsonFieldType.BOOLEAN).description("비어 있는 페이지 여부"),
                                fieldWithPath("result.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("result.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부")
                        )
                ));
    }

    @DisplayName("정책 상세 조회 API 테스트")
    @Test
    @WithMockUser
    void getPolicyTest() throws Exception {
        // given
        PolicyInfoResponseDto response = createPolicyInfoResponseDto();

        when(policyService.getPolicy(any(User.class), any(Long.class)))
                .thenReturn(response);
        // when & then
        mockMvc.perform(get("/policy/{policyId}", 1L)
                        .header("Authorization", "access token")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("policy-get",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("policyId").description("정책 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.policyId").type(JsonFieldType.NUMBER).description("정책 ID"),
                                fieldWithPath("result.policyName").type(JsonFieldType.STRING).description("정책 이름"),
                                fieldWithPath("result.policyApplyDenialReason").type(JsonFieldType.STRING).description("정책 불가 사유"),
                                fieldWithPath("result.policyIntroduction").type(JsonFieldType.STRING).description("정책 소개"),
                                fieldWithPath("result.policyApplyDocument").type(JsonFieldType.STRING).description("신청 서류"),
                                fieldWithPath("result.policyApplyMethod").type(JsonFieldType.STRING).description("신청 방법"),
                                fieldWithPath("result.policyApplyDate").type(JsonFieldType.STRING).description("신청 기간"),
                                fieldWithPath("result.policyDateType").type(JsonFieldType.STRING).description(generateLinkCode(POLICY_DATE_TYPE)),
                                fieldWithPath("result.policyDateTypeDescription").type(JsonFieldType.STRING).description("신청 기간 타입 설명"),
                                fieldWithPath("result.applicationSite").type(JsonFieldType.STRING).description("신청 URL"),
                                fieldWithPath("result.referenceSite").type(JsonFieldType.STRING).description("참고 URL"),
                                fieldWithPath("result.benefit").type(JsonFieldType.NUMBER).description("수혜 금액"),
                                fieldWithPath("result.benefitPeriod").type(JsonFieldType.STRING).description("수혜 금액 기간"),
                                fieldWithPath("result.applyFlag").type(JsonFieldType.BOOLEAN).description("수혜(신청) 정책 여부"),
                                fieldWithPath("result.interestFlag").type(JsonFieldType.BOOLEAN).description("관심 정책 여부"),
                                fieldWithPath("result.policyMethodType").type(JsonFieldType.STRING).description(generateLinkCode(POLICY_METHOD_TYPE)),
                                fieldWithPath("result.policyMethodTypeDescription").type(JsonFieldType.STRING).description("정책 신청 방법 설명")
                        )
                ));
    }

    @DisplayName("특정 달에 신청일이 포함된 관심 정책 리스트 조회 API 테스트")
    @Test
    @WithMockUser
    void getPolicesBySearchMonthTest() throws Exception {
        // given
        List<CalendarPolicyListResponseDto> response = createCalendarPolicyListResponseDtoList();

        when(policyService.getPolicyListBySearchMonth(any(User.class), any(LocalDate.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/policy/calendar")
                        .param("searchDate", "2024-08-20")
                        .header("Authentication", "Access Token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("policy-calendar-get",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("searchDate").description("조회 하려는 특정 달").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.ARRAY).description("응답 결과"),
                                fieldWithPath("result[].policyId").type(JsonFieldType.NUMBER).description("정책 ID"),
                                fieldWithPath("result[].policyName").type(JsonFieldType.STRING).description("정책 이름"),
                                fieldWithPath("result[].applyStatus").type(JsonFieldType.BOOLEAN).description("정책 신청 가능 상태"),
                                fieldWithPath("result[].applyProcedure").type(JsonFieldType.STRING).description("정책 신청 방법"),
                                fieldWithPath("result[].policyAgencyLogo").type(JsonFieldType.STRING).description("정책 기관 로고 url"),
                                fieldWithPath("result[].applySttDate").type(JsonFieldType.STRING).description("정책 신청 시작일"),
                                fieldWithPath("result[].applyEndDate").type(JsonFieldType.STRING).description("정책 신청 종료일"),
                                fieldWithPath("result[].policyApplyDenialReason").type(JsonFieldType.STRING).description("정책 불가 사유")
                        )
                ));
    }

    @DisplayName("특정 날짜에 따른 신청 시작일과 신청 종료일을 가지는 관심 정책 리스트 조회 API 테스트")
    @Test
    @WithMockUser
    void getPolicesBySearchDateTest() throws Exception {
        // given
        List<CalendarPolicyListResponseDto> response = createCalendarPolicyListResponseDtoList();

        when(policyService.getPolicyListBySearchDate(any(User.class), any(LocalDate.class), any(SearchDateType.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/policy/calendar/date")
                        .param("searchDate", "2024-08-20")
                        .param("searchDateType", "END_DATE")
                        .header("Authentication", "Access Token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("policy-calendar-date-get",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("searchDate").description("조회 하려는 특정 날짜").optional(),
                                parameterWithName("searchDateType").description("시작일 타입").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.ARRAY).description("응답 결과"),
                                fieldWithPath("result[].policyId").type(JsonFieldType.NUMBER).description("정책 ID"),
                                fieldWithPath("result[].policyName").type(JsonFieldType.STRING).description("정책 이름"),
                                fieldWithPath("result[].applyStatus").type(JsonFieldType.BOOLEAN).description("정책 신청 가능 상태"),
                                fieldWithPath("result[].applyProcedure").type(JsonFieldType.STRING).description("정책 신청 방법"),
                                fieldWithPath("result[].policyAgencyLogo").type(JsonFieldType.STRING).description("정책 기관 로고 url"),
                                fieldWithPath("result[].applySttDate").type(JsonFieldType.STRING).description("정책 신청 시작일"),
                                fieldWithPath("result[].applyEndDate").type(JsonFieldType.STRING).description("정책 신청 종료일"),
                                fieldWithPath("result[].policyApplyDenialReason").type(JsonFieldType.STRING).description("정책 불가 사유")
                        )
                ));
    }

    @DisplayName("지원 정책 유형에 따른 추천 정책 API 테스트")
    @Test
    @WithMockUser
    void recommendTest() throws Exception {
        // given
        RecommendPolicyInfoResponseDto response = createRecommendPolicyInfoResponseDto();

        when(policyService.recommend(any(User.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/policy/recommend")
                        .header("Authentication", "Access Token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("policy-recommend-get",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.policyInfos").type(JsonFieldType.ARRAY).description("추천 정책 정보 목록"),
                                fieldWithPath("result.policyInfos[].supportType").type(JsonFieldType.STRING).description("정책 지원 유형"),
                                fieldWithPath("result.policyInfos[].supportTypeDescription").type(JsonFieldType.STRING).description("정책 지원 유형 이름"),
                                fieldWithPath("result.policyInfos[].policyId").type(JsonFieldType.NUMBER).description("정책 ID"),
                                fieldWithPath("result.policyInfos[].policyName").type(JsonFieldType.STRING).description("정책 이름"),
                                fieldWithPath("result.policyInfos[].policyLogo").type(JsonFieldType.STRING).description("정책 지역 로고"),
                                fieldWithPath("result.policyInfos[].policyAreaCode").type(JsonFieldType.STRING).description(generateLinkCode(AREA_CODE)),
                                fieldWithPath("result.policyInfos[].policyCityCode").type(JsonFieldType.STRING).description(generateLinkCode(CITY_CODE)),
                                fieldWithPath("result.policyInfos[].policyIntroduction").type(JsonFieldType.STRING).description("정책 소개"),
                                fieldWithPath("result.policyInfos[].supportTypePolicyCnt").type(JsonFieldType.NUMBER).description("지원 정책 별 정책 수"),
                                fieldWithPath("result.policyInfos[].applyFlag").type(JsonFieldType.BOOLEAN).description("수혜(신청) 정책 여부"),
                                fieldWithPath("result.policyInfos[].interestFlag").type(JsonFieldType.BOOLEAN).description("관심 정책 여부"),
                                fieldWithPath("result.policyInfos[].benefit").type(JsonFieldType.NUMBER).description("정책 수혜금액"),
                                fieldWithPath("result.policyInfos[].policyDateType").type(JsonFieldType.STRING).description("정책 기간 타입"),
                                fieldWithPath("result.policyInfos[].policyMethodType").type(JsonFieldType.STRING).description(generateLinkCode(POLICY_METHOD_TYPE)),
                                fieldWithPath("result.policyInfos[].policyMethodTypeDescription").type(JsonFieldType.STRING).description("정책 신청 방법 설명")
                        )
                ));
    }

    @DisplayName("지원할 수 있는 정책의 수 조회 API 테스트")
    @Test
    @WithMockUser
    void getRecommendCountTest() throws Exception {
        // given
        PolicyCountResponseDto response = PolicyCountResponseDto.builder()
                .nickname("paul")
                .policyCnt(10)
                .build();

        when(policyService.getRecommendCountAndNickname(any(User.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/policy/recommend/count")
                        .header("Authentication", "Access Token")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("policy-recommend-count-get",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답코드"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 결과"),
                                fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("result.policyCnt").type(JsonFieldType.NUMBER).description("유저에 맞는 정책의 총 수")
                        )
                ));
    }

    private List<CalendarPolicyListResponseDto> createCalendarPolicyListResponseDtoList() {
        return List.of(createCalendarPolicyListResponseDto(1L), createCalendarPolicyListResponseDto(2L));
    }

    private CalendarPolicyListResponseDto createCalendarPolicyListResponseDto(Long policyId) {
        return CalendarPolicyListResponseDto.builder()
                .policyId(policyId)
                .policyName("test")
                .applyStatus(false)
                .applyProcedure(PolicyMethodType.LETTER)
                .policyAgencyLogo("test url")
                .applySttDate(LocalDate.of(2024, 6, 20))
                .applyEndDate(LocalDate.of(2024, 12, 31))
                .policyApplyDenialReason("test Reason")
                .build();
    }

    private RecommendPolicyInfoResponseDto createRecommendPolicyInfoResponseDto() {
        return RecommendPolicyInfoResponseDto.builder()
                .policyInfos(List.of(createRecommendPolicyInfo(SupportPolicyType.MONEY), createRecommendPolicyInfo(SupportPolicyType.LOANS), createRecommendPolicyInfo(SupportPolicyType.SOCIAL_SERVICE)))
                .build();
    }

    private RecommendPolicyInfoResponseDto.recommendPolicyInfo createRecommendPolicyInfo(SupportPolicyType supportPolicyType) {
        return RecommendPolicyInfoResponseDto.recommendPolicyInfo.builder()
                .supportType(supportPolicyType) // 예제: 지원 유형 (SupportPolicyType 열거형)
                .supportTypeDescription(supportPolicyType.getDescription()) // 예제: 지원 유형 설명
                .policyId(24L) // 예제: 정책 ID
                .policyName("청년도약계좌") // 예제: 정책 이름
                .policyLogo("https://example.com/logo.png") // 예제: 정책 로고
                .policyAreaCode("서울") // 예제: 정책 시/도 이름
                .policyCityCode("강서구") // 예제: 정책 시/구 이름
                .policyIntroduction("월 70만원을 5년 납입하면 약 5,000만원을 적립할 수 있는 청년도약계좌") // 예제: 정책 소개
                .supportTypePolicyCnt(24) // 예제: 지원 정책 별 정책 수
                .applyFlag(true) // 예제: 수혜(신청) 정책 여부
                .interestFlag(false) // 예제: 관심 정책 여부
                .benefit(1560000) // 예제: 정책 수혜금액
                .policyDateType("CONSTANT") // 예제: 정책 기간 타입
                .policyMethodType(PolicyMethodType.ONLINE) // 예제: 정책 신청 방법 (PolicyMethodType 열거형)
                .policyMethodTypeDescription("온라인신청") // 예제: 정책 신청 방법 설명
                .build();
    }

    private PolicyInfoResponseDto createPolicyInfoResponseDto() {
        return PolicyInfoResponseDto.builder()
                .policyId(12L)
                .policyName("청년도약계좌")
                .policyApplyDenialReason("신청 기간이 아니에요, ~조건이 맞지 않습니다.")
                .policyIntroduction("월 70만원을 5년 납입하면 약 5,000만원을 적립할 수 있는 청년도약계좌")
                .policyApplyDocument("주민등록등본, 신분증, 소득증빙서류 등")
                .policyApplyMethod("주소지 읍·면·동 행정복지센터 방문 또는 등기우편 신청")
                .policyApplyDate("2023년 6월 15일 ~ 2023년 12월 31일")
                .policyDateType(PolicyDateType.PERIOD)
                .policyDateTypeDescription("기간 신청")
                .applicationSite("https://www.kinfa.or.kr/product/youthJump.do")
                .referenceSite("https://www.kinfa.or.kr/product/youthJump.do")
                .benefit(new BigDecimal("1000000"))
                .benefitPeriod("년")
                .applyFlag(true)
                .interestFlag(true)
                .policyMethodType(PolicyMethodType.LETTER)
                .policyMethodTypeDescription("우편신청")
                .build();

    }

    private PolicyListResponseDto creatPolicyListResponseDto() {
        Page<PolicyListInfoDto> policyListInfoDtoPage = createPolicyListInfoDtoPage();
        return PolicyListResponseDto.builder()
                .policyListInfoResponseDto(policyListInfoDtoPage)
                .pageNumber(0)
                .last(false)
                .build();
    }

    private Page<PolicyListInfoDto> createPolicyListInfoDtoPage() {
        List<PolicyListInfoDto> policyListInfoDtoList = List.of(createPolicyListInfoDto(1L), createPolicyListInfoDto(2L));
        return new PageImpl<>(policyListInfoDtoList, PageRequest.of(0, 10), policyListInfoDtoList.size());
    }

    private PolicyListInfoDto createPolicyListInfoDto(Long policyId) {
        return PolicyListInfoDto.builder()
                .policyId(policyId)
                .policyName("Test Policy")
                .policyLogo("http://example.com/logo.png")
                .policyIntroduction("This is a test policy introduction.")
                .areaCode("서울")
                .cityCode("강남구")
                .policyDateType(PolicyDateType.PERIOD)
                .policyDateTypeDescription("기간 신청")
                .policyApplyDenialReason("신청 기간이 아닙니다.")
                .applyStatus(false)
                .benefit(BigDecimal.valueOf(1000000))
                .benefitPeriod("년")
                .applyFlag(true)
                .interestFlag(false)
                .policyMethodType(PolicyMethodType.ONLINE)
                .policyMethodTypeDescription("온라인신청")
                .policyUrl("http://example.com/apply")
                .build();
    }

}
