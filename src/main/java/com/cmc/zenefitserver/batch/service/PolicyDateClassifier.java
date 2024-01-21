package com.cmc.zenefitserver.batch.service;

import com.cmc.zenefitserver.domain.policy.domain.ApplyPeriod;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyDateType;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PolicyDateClassifier {
    private static final String URL = "https://www.youthcenter.go.kr/youngPlcyUnif/youngPlcyUnifDtl.do?bizId=";

    /**
     * 1. 상시
     * 2. 미정
     * 3. 날짜데이터
     * 3-1. 매년 ( 현재 날짜의 년도를 붙임 )
     * 3-2. 매월 ( 현재 날짜의 년도와 1월부터 12월까지 모두 넣음 )
     * 3-3. 여러 날짜 데이터
     * => 다 저장하고, endDate 를 기준으로 정렬 후 현재날짜 이후에 나오는 첫번째 endDate 찾아서 해당 하는 정책의 신청시작일과 신청종료일을 분류함
     * 3-4. 하나의 날짜 데이터
     * => 정책의 신청시작일과 신청종료일을 저장
     * 4. 빈값 ( 비고 데이터 값을 비교해보자 )
     * 4-1.
     * 4-2.
     */
    public void classify(Policy policy) {

        LocalDate now = LocalDate.now();

        final String newUrl = URL + policy.getBizId();

        Document document = null;
        try {
            document = Jsoup.connect(newUrl).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Elements elements = document.select(".policy-detail");
        for (Element element : elements) {
            Elements businessApplicationPeriodElement = element.select(".list_tit:contains(사업 신청 기간)");
            Elements remarkElement = element.select(".list_tit:contains(비고)");

            String businessApplicationPeriodText = businessApplicationPeriodElement.get(0).nextElementSibling().text();
            String remarkText = remarkElement.get(0).nextElementSibling().text();

            // 1. 상시
            if (businessApplicationPeriodText.contains("상시")) {
                policy.updatePolicyDateType(PolicyDateType.CONSTANT);
                break;
            }

            // 2. 미정
            if (businessApplicationPeriodText.contains("미정")) {
                policy.updatePolicyDateType(PolicyDateType.UNDECIDED);
                break;
            }

            // 4. 빈값 -> 비고 확인
            if (businessApplicationPeriodText.equals("")) {
                policy.updatePolicyDateType(PolicyDateType.BLANK);
                break;
//                    System.out.println("bizId : " + policy.getBizId() + " 비고 : " + remarkText);
//                    policy.updateRemark(remarkText);
            }
            // 3-1. 매년
            if (businessApplicationPeriodText.contains("매년")) {
                Pattern pattern = Pattern.compile("\\d{2}월\\d{2}일~\\d{2}월\\d{2}일");

                Matcher matcher = pattern.matcher(businessApplicationPeriodText.replace(" ", ""));

                List<ApplyPeriod> applyPeriods = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년MM월dd일");
                while (matcher.find()) {
                    String[] splited = matcher.group().split("~");
                    StringBuilder sttDate = new StringBuilder();
                    sttDate.append("2023년");
                    sttDate.append(splited[0]);
                    StringBuilder endDate = new StringBuilder();
                    endDate.append("2023년");
                    endDate.append(splited[1]);

                    applyPeriods.add(ApplyPeriod.builder().sttDate(LocalDate.parse(sttDate, formatter)).endDate(LocalDate.parse(endDate, formatter)).build());
                }
                policy.updatePolicyDateType(PolicyDateType.PERIOD);
                updateApplySttDateAndApplyEndDate(policy, applyPeriods, now);
                break;
            }

            // 3-2. 매월
            if (businessApplicationPeriodText.contains("매월")) {
                Pattern pattern = Pattern.compile("\\d{2}일~\\d{2}일");

                Matcher matcher = pattern.matcher(businessApplicationPeriodText.replace(" ", ""));

                List<ApplyPeriod> applyPeriods = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년MM월dd일");
                while (matcher.find()) {
                    String[] splited = matcher.group().split("~");

                    for (int i = 1; i <= 12; i++) {
                        StringBuilder sttDate = new StringBuilder();
                        StringBuilder endDate = new StringBuilder();
                        sttDate.append("2023년");
                        endDate.append("2023년");
                        String month = String.format("%02d", i);
                        sttDate.append(month + "월").append(splited[0]);
                        endDate.append(month + "월").append(splited[1]);
                        applyPeriods.add(ApplyPeriod.builder().sttDate(LocalDate.parse(sttDate, formatter)).endDate(LocalDate.parse(endDate, formatter)).build());
                    }

                }
                policy.updatePolicyDateType(PolicyDateType.PERIOD);
                updateApplySttDateAndApplyEndDate(policy, applyPeriods, now);
                policy.updateApplyPeriods(applyPeriods);
                break;
            }

            // 정규 표현식 패턴
            Pattern pattern = Pattern.compile("\\d{4}년\\d{2}월\\d{2}일~\\d{4}년\\d{2}월\\d{2}일");

            // Matcher를 사용하여 패턴과 일치하는 부분을 찾아냄
            Matcher matcher = pattern.matcher(businessApplicationPeriodText.replace(" ", ""));

            List<ApplyPeriod> applyPeriods = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년MM월dd일");
            while (matcher.find()) {
                String[] splited = matcher.group().split("~");
                applyPeriods.add(ApplyPeriod.builder().sttDate(LocalDate.parse(splited[0], formatter)).endDate(LocalDate.parse(splited[1], formatter)).build());
            }

//                Collections.sort(applyPeriods);
            // 3-3. 하나의 이상의 날짜 데이터
            if (applyPeriods.size() > 0) {
                policy.updatePolicyDateType(PolicyDateType.PERIOD);
                updateApplySttDateAndApplyEndDate(policy, applyPeriods, now);
                policy.updateApplyPeriods(applyPeriods);
                break;
            }

            log.debug("policyXXId : {}", policy.getId());
            log.debug("businessApplicationPeriodText : {}", businessApplicationPeriodText);
        }

    }


    private static void updateApplySttDateAndApplyEndDate(Policy policy, List<ApplyPeriod> applyPeriods, LocalDate now) {
        ApplyPeriod period = applyPeriods.stream()
                .filter(p -> p.getEndDate().isAfter(LocalDate.now()))
                .findFirst()
                .orElse(null);

        if (period != null) {
            policy.updateApplySttDateAndApplyEndDate(period.getSttDate(), period.getEndDate());

        }
    }
}