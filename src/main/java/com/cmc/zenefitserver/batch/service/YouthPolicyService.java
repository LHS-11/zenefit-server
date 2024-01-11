package com.cmc.zenefitserver.batch.service;

import com.cmc.zenefitserver.domain.policy.domain.YouthPolicy;
import com.cmc.zenefitserver.domain.policy.domain.YouthPolicyList;
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
import java.util.Arrays;
import java.util.List;
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
        try {
            String xmlData = getXmlDataFromApi(String.valueOf(pageIndex));
            xmlData = removeInvalidCharacters(xmlData);

            JAXBContext jaxbContext = JAXBContext.newInstance(YouthPolicyList.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            InputStream inputStream = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));
            YouthPolicyList youthPolicyList = (YouthPolicyList) unmarshaller.unmarshal(inputStream);
            List<YouthPolicy> youthPolicies = Arrays.stream(youthPolicyList.getYouthPolicies())
                    .collect(Collectors.toList());
            return youthPolicies;
        } catch (IOException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public String getXmlDataFromApi(String pageIndex) throws IOException {

        HttpHeaders headers = new HttpHeaders();


        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("openApiVlak", key)
                .queryParam("display", displayCnt)
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
            return getXmlDataFromApi(pageIndex);
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
