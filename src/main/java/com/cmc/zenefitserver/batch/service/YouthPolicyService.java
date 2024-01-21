package com.cmc.zenefitserver.batch.service;

import com.cmc.zenefitserver.domain.policy.domain.YouthPolicy;
import com.cmc.zenefitserver.domain.policy.domain.YouthPolicyList;
import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class YouthPolicyService {

    private final RestTemplate restTemplate;
    private final int displayCnt;
    private final String apiUrl;
    private final String key;

    public YouthPolicyService(RestTemplate restTemplate, @Value("${policy.api-url}") String apiUrl, @Value("${policy.api-key}") String key) {
        this.restTemplate = restTemplate;
        this.displayCnt = 100;
        this.apiUrl = apiUrl;
        this.key = key;
    }

    public List<YouthPolicy> getYouthPolices(int pageIndex) {
        List<YouthPolicy> youthPolices = new ArrayList<>();
        Set<String> existingBizIds = new HashSet<>();

        for (AreaCode areaCode : AreaCode.values()) {
            List<YouthPolicy> youthPolicesByAreaCode = getYouthPolices(pageIndex, areaCode, null);

            for (CityCode cityCode : areaCode.getCities()) {
                List<YouthPolicy> youthPolicesByCityCode = getYouthPolices(pageIndex, areaCode, cityCode);

                if (youthPolicesByCityCode != null) {
                    youthPolices.addAll(youthPolicesByCityCode);
                    existingBizIds.addAll(youthPolicesByCityCode.stream().map(YouthPolicy::getBizId).collect(Collectors.toList()));
                }
            }

            if (youthPolicesByAreaCode != null) {
                List<YouthPolicy> filterYouthPolices = youthPolicesByAreaCode.stream().filter(youthPolicy -> !existingBizIds.contains(youthPolicy.getBizId())).collect(Collectors.toList());
                youthPolices.addAll(filterYouthPolices);
            }

        }
        return youthPolices;
    }

    private List<YouthPolicy> getYouthPolices(int pageIndex, AreaCode areaCode, CityCode cityCode) {
        try {

            String xmlData = getXmlDataFromApi(String.valueOf(pageIndex), cityCode == null ? areaCode : cityCode);
            xmlData = removeInvalidCharacters(xmlData);

            JAXBContext jaxbContext = JAXBContext.newInstance(YouthPolicyList.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            InputStream inputStream = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));
            YouthPolicyList youthPolicyList = (YouthPolicyList) unmarshaller.unmarshal(inputStream);
            if (youthPolicyList.getYouthPolicies() != null) {
                List<YouthPolicy> youthPolicies = Arrays.stream(youthPolicyList.getYouthPolicies()).collect(Collectors.toList());
                youthPolicies.stream().forEach(youthPolicy -> youthPolicy.updateRegion(areaCode, cityCode));
                return youthPolicies;
            }
            return null;
        } catch (IOException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public String getXmlDataFromApi(String pageIndex, Enum<?> codeEnum) throws IOException {

        HttpHeaders headers = new HttpHeaders();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("openApiVlak", key)
                .queryParam("display", displayCnt)
                .queryParam("pageIndex", pageIndex)
                .queryParam("srchPolyBizSecd", getCodeFromEnum(codeEnum));
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
            return getXmlDataFromApi(pageIndex, codeEnum);
        }

    }

    private static String removeInvalidCharacters(String xmlData) {
        if (xmlData == null) {
            return null;
        }
        // 유효하지 않은 문자(범위 [0x00-0x08], [0x0B-0x0C], [0x0E-0x1F])를 삭제
        return xmlData.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
    }

    private String getCodeFromEnum(Enum<?> codeEnum) {
        if (codeEnum instanceof AreaCode) {
            return ((AreaCode) codeEnum).getCode();
        } else if (codeEnum instanceof CityCode) {
            return ((CityCode) codeEnum).getCode();
        }
        throw new IllegalArgumentException("Unsupported enum type");
    }

}
