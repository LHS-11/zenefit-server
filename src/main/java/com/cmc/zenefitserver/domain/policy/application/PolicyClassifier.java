package com.cmc.zenefitserver.domain.policy.application;

import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.YouthPolicyList;
import com.cmc.zenefitserver.domain.policy.domain.YouthPolicy;
import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpEntity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PolicyClassifier {

    public static final String URL = "https://www.youthcenter.go.kr/opi/youthPlcyList.do";
    public static final int DISPLAY_CNT = 100;

    @Value("${policy.api-key}")
    private String KEY;

    private final PolicyRepository policyRepository;
    private final PolicyAgeClassifier policyAgeClassifier;
    private final PolicyEmpmClassifier policyEmpmClassifier;
    private final PolicyEduClassifier policyEduClassifier;
    private final PolicySplzClassifier policySplzClassifier;
    private final RestTemplate restTemplate;

    @Transactional
    public void savePolicyInfo() {

        try {
            List<Policy> getpolicies = getpolicies(URL, 50);
            policyRepository.saveAll(getpolicies);
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Policy> getpolicies(String apiUrl, int limit) throws Exception {
        List<Policy> list = new ArrayList<>();
        for (int pageIndex = 1; pageIndex <= limit; pageIndex++) {
            String xmlData = getXmlDataFromApi(apiUrl, String.valueOf(pageIndex));
            xmlData = removeInvalidCharacters(xmlData);

            JAXBContext jaxbContext = JAXBContext.newInstance(YouthPolicyList.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            InputStream inputStream = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));
            YouthPolicyList youthPolicyList = (YouthPolicyList) unmarshaller.unmarshal(inputStream);
            YouthPolicy[] youthPolicies = youthPolicyList.getYouthPolicies();

            if (youthPolicies == null) {
                break;
            }
            List<Policy> policies = mapYouthPoliciesToPolicyList(youthPolicies);
            list.addAll(policies);
        }
        return list;
    }

    private List<Policy> mapYouthPoliciesToPolicyList(YouthPolicy[] youthPolicies) {
        List<Policy> policies = Arrays.stream(youthPolicies)
                .map(p -> {
                            Policy policy = Policy.builder()
                                    .bizId(p.getBizId())
                                    .policyName(p.getPolyBizSjnm())
                                    .policyIntroduction(p.getPolyItcnCn())
                                    .operatingAgencyName(p.getCnsgNmor())
                                    .applicationPeriodContent(p.getRqutPrdCn())
                                    .organizationType(p.getPolyBizTy())
                                    .supportContent(p.getSporCn())
                                    .ageInfo(p.getAgeInfo())
                                    .employmentStatusContent(p.getEmpmSttsCn())
                                    .specializedFieldContent(p.getSplzRlmRqisCn())
                                    .educationalRequirementContent(p.getAccrRqisCn())
                                    .residentialAndIncomeRequirementContent(p.getPrcpCn())
                                    .additionalClauseContent(p.getAditRscn())
                                    .eligibilityTargetContent(p.getPrcpLmttTrgtCn())
                                    .applicationSiteAddress(p.getJdgnPresCn())
                                    .referenceSiteUrlAddress(p.getRfcSiteUrla1())
                                    .applicationProcedureContent(p.getRqutProcCn())
                                    .submissionDocumentContent(p.getPstnPaprCn())
                                    .policyCode(PolicyCode.findPolicyCode(p.getPolyRlmCd()))
                                    .build();

                            policy.updateJobTypes(policyEmpmClassifier.mapToJobTypesFromEmpmContent(p.getEmpmSttsCn()));
                            policy.updateEducationTypes(policyEduClassifier.mapToEducationTypeFromEduContent(p.getAccrRqisCn()));
                            policy.updateSplzTypes(policySplzClassifier.mapToSplzCodeFromSplzContent(p.getSplzRlmRqisCn()));
                            policyAgeClassifier.setMaxAgeAndMinAge(policy);
                            return policy;
                        }
                )
                .collect(Collectors.toList());
        return policies;
    }

    public String getXmlDataFromApi(String apiUrl, String pageIndex) throws IOException {

        HttpHeaders headers = new HttpHeaders();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("openApiVlak", KEY)
                .queryParam("display", DISPLAY_CNT)
                .queryParam("pageIndex", pageIndex);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

            String body = response.getBody();
            if (body.contains("error")) {
                System.out.println("error 발생");
                throw new RestClientException("error 발생");
            }
            return response.getBody();

        } catch (RestClientException e) {
            return getXmlDataFromApi(apiUrl, pageIndex);
        }

    }

    private static String removeInvalidCharacters(String xmlData) {
        if (xmlData == null) {
            return null;
        }
        // 유효하지 않은 문자(범위 [0x00-0x08], [0x0B-0x0C], [0x0E-0x1F])를 삭제
        return xmlData.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
    }

}
