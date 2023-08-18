package com.cmc.zenefitserver.domain.policy.application;

import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.YouthPolicy;
import com.cmc.zenefitserver.domain.policy.domain.YouthPolicyList;
import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.cmc.zenefitserver.domain.policy.application.PolicyClassifier.DISPLAY_CNT;
import static com.cmc.zenefitserver.domain.policy.application.PolicyClassifier.URL;

@RequiredArgsConstructor
@Service
public class PolicyAreaClassifier {

    @Value("${policy.api-key}")
    private String KEY;

    private final PolicyRepository policyRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public void updateCentralGovernment() {
        List<Policy> policies = policyRepository.findAll();
        policies
                .stream()
                .filter(policy -> policy.getAreaCode() == null)
                .forEach(policy -> policy.updateAreaCode(AreaCode.CENTRAL_GOVERNMENT));

    }

    @Transactional
    public void updateAreaCode() {
        Map<String, Policy> policyMap = new HashMap<>();
        List<Policy> policies = new ArrayList<>();

        for (AreaCode areaCode : AreaCode.values()) {
            List<Policy> policesToList = getPolicesToList(URL, 1, 40, areaCode);
            if (policesToList != null) {
                policies.addAll(policesToList);
            }
        }

        for (AreaCode areaCode : AreaCode.values()) {
            for (CityCode cityCode : areaCode.getCities()) {
                List<Policy> policesToList = getPolicesToList(URL, 1, 2, cityCode);
                if (policesToList != null) {
                    policies.addAll(policesToList);
                }
            }
        }

        // 중복을 제거한 후 일괄 삽입
        policyRepository.saveAll(policies);
    }

    @Transactional
    public void updateCityCode() {
        List<Policy> policies = new ArrayList<>();

        Arrays.stream(AreaCode.values())
                .filter(areaCode -> areaCode != AreaCode.CENTRAL_GOVERNMENT)
                .map(a -> a.getCities())
                .forEach(cityCodes -> {
                    cityCodes.stream()
                            .forEach(cityCode -> {
                                List<Policy> policesToList = getPolicesToList(URL, 1, 2, cityCode);
                                if (policesToList != null) {
                                    policies.addAll(policesToList);
                                }
                            });
                });

        // 중복을 제거한 후 일괄 삽입
        policyRepository.saveAll(policies);
    }


    private List<Policy> getPolicesToList(String apiUrl, int pageIndex, int limit, Enum<?> codeEnum) {
        try {
            List<Policy> policies = new ArrayList<>();
            for (; pageIndex <= limit; pageIndex++) {
                String xmlData = fetchXmlDataFromApi(apiUrl, String.valueOf(pageIndex), codeEnum);
                xmlData = removeInvalidCharacters(xmlData);

                JAXBContext jaxbContext = JAXBContext.newInstance(YouthPolicyList.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                InputStream inputStream = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));
                YouthPolicyList youthPolicyList = (YouthPolicyList) unmarshaller.unmarshal(inputStream);
                YouthPolicy[] youthPolicies = youthPolicyList.getYouthPolicies();


                if (youthPolicies == null) {
                    break;
                }

                if (youthPolicies != null) {
                    for (YouthPolicy p : youthPolicies) {
                        Policy policy = policyRepository.findByBizId(p.getBizId())
                                .orElseThrow(() -> new IllegalArgumentException("해당하는 정책이 데이터베이스에 존재하지 않습니다."));

                        if (codeEnum instanceof AreaCode) {
                            policy.updateAreaCode((AreaCode) codeEnum);
                        }
                        if (codeEnum instanceof CityCode) {
                            policy.updateCityCode((CityCode) codeEnum);
                        }

                        // policyMap에 policy를 추가 또는 업데이트
                        policies.add(policy);
                    }
                }
            }
            return policies;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void getPolices(Map<String, Policy> policyMap, String apiUrl, int pageIndex, int limit, Enum<?> codeEnum) {
        try {
            for (; pageIndex <= limit; pageIndex++) {
                String xmlData = fetchXmlDataFromApi(apiUrl, String.valueOf(pageIndex), codeEnum);
                xmlData = removeInvalidCharacters(xmlData);

                JAXBContext jaxbContext = JAXBContext.newInstance(YouthPolicyList.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                InputStream inputStream = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));
                YouthPolicyList youthPolicyList = (YouthPolicyList) unmarshaller.unmarshal(inputStream);
                YouthPolicy[] youthPolicies = youthPolicyList.getYouthPolicies();


                if (youthPolicies == null) {
                    break;
                }

                if (youthPolicies != null) {
                    for (YouthPolicy p : youthPolicies) {
                        Policy policy = policyRepository.findByBizId(p.getBizId())
                                .orElseThrow(() -> new IllegalArgumentException("해당하는 정책이 데이터베이스에 존재하지 않습니다."));

                        if (codeEnum instanceof AreaCode) {
                            policy.updateAreaCode((AreaCode) codeEnum);
                        }
                        if (codeEnum instanceof CityCode) {
                            policy.updateCityCode((CityCode) codeEnum);
                        }

                        // policyMap에 policy를 추가 또는 업데이트
                        policyMap.put(policy.getBizId(), policy);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String fetchXmlDataFromApi(String apiUrl, String pageIndex, Enum<?> codeEnum) throws IOException {
        HttpHeaders headers = new HttpHeaders();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("openApiVlak", KEY)
                .queryParam("display", DISPLAY_CNT)
                .queryParam("pageIndex", pageIndex)
                .queryParam("srchPolyBizSecd", getCodeFromEnum(codeEnum));
//        System.out.println("builder.toUriString() = " + builder.toUriString());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

            String body = response.getBody();
            if (body.contains("error")) {
                System.out.println("error 발생");
                throw new RestClientException("error 발생");
            }
            return body;
        } catch (RestClientException e) {
            return fetchXmlDataFromApi(apiUrl, pageIndex, codeEnum);
        }
    }

    private String getCodeFromEnum(Enum<?> codeEnum) {
        if (codeEnum instanceof AreaCode) {
            return ((AreaCode) codeEnum).getCode();
        } else if (codeEnum instanceof CityCode) {
            return ((CityCode) codeEnum).getCode();
        }
        throw new IllegalArgumentException("Unsupported enum type");
    }

    private static String removeInvalidCharacters(String xmlData) {
        if (xmlData == null) {
            return null;
        }
        // 유효하지 않은 문자(범위 [0x00-0x08], [0x0B-0x0C], [0x0E-0x1F])를 삭제
        return xmlData.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
    }

}
