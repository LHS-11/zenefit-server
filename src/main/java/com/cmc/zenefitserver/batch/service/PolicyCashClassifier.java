package com.cmc.zenefitserver.batch.service;

import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import com.cmc.zenefitserver.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cmc.zenefitserver.global.error.ErrorCode.NOT_FOUND_MONEY_VALUE;

@RequiredArgsConstructor
@Service
public class PolicyCashClassifier {


    /**
     * todo
     *
     * 변경 전
     * 1. 한줄 씩 문장마다 쪼개기 ( * 삭제 후 )
     * 2. 만원, 천원, 원에 해당하는 리스트를 만들어서 각 문장마다 각각 찾아서 앞에 5글자까지 가져와서, 해당 숫자와 저장 ( 기존의 맨 처음에 찾았던 로직 -> 모두 찾는 것으로 변경 )
     * 3. 리스트들을 모두 하나의 리스트로 만들고, 해당 리스트들에서 당, 년, 월, 원(금액만 존재) 으로 나눠서 [ 금액, 기간 (금액만 존재할시 임의로) ] 하나의 최종 리스트에 저장
     *  ( 우선 순위  1.당 2.월 3.년 4.원(금액만 존재) 순으로, 기존의 로직에서 당, 월, 년 순으로 찾으면 루프 종료하는 것을 참조 )
     *  4. 최종 리스트들마다 당, 월, 년, 원(금액만 존재) 으로 따로 분류해서 각각 당, 월, 년, 원(금액만 존재) 리스트로 나눠서 저장
     *  5. 당에 해당하는 리스트에 값이 존재시 해당 리스트에서 금액 최댓값을 해당 정책의 수혜금액으로 산출
     *  5-1. 월에 해당하는 리스트에 값이 존재시 해당 리스트에서 금액 최댓값을 해당 정책의 수혜금액으로 산출
     *  5-2. 년에 해당하는 리스트에 값이 존재시 해당 리스트에서 금액 최댓값을 해당 정책의 수혜금액으로 산출
     *  5-3. 원(금액만 존재)에 해당하는 리스트에 값이 존재시 해당 리스트에서 금액 최댓값을 해당 정책의 수혜금액으로 산출
     *
     *
     * 변경 후
     * 1. 한줄 씩 문장마다 쪼개기 ( * 삭제 후 )
     * 2. 천만원, 백만원, 십만원 ,억원, 천원, 원에 해당하는 정규식으로 각 문장마다 각각 찾아서 앞에 5글자까지 가져와서, 해당 숫자와 저장 ( 기존의 맨 처음에 찾았던 로직 -> 모두 찾는 것으로 변경 )
     * 3. 세대당, 분기당, 개월 등과 같은 문장이 있을 때, 1개월을 기준으로 수혜금액 나누기
     * 4. 해당 리스트에서 당, 년, 월, 원(금액만 존재) 으로 나눠서 [ 금액, 기간 (금액만 존재할시 임의로) ] 하나의 최종 리스트에 저장
     * ( 우선 순위  1.당 2.월 3.년 4.원(금액만 존재) 순으로, 기존의 로직에서 당, 월, 년 순으로 찾으면 루프 종료하는 것을 참조 )
     * 5. 당에 해당하는 리스트에 값이 존재시 해당 리스트에서 금액 최댓값을 해당 정책의 수혜금액으로 산출
     * 5-1. 월에 해당하는 리스트에 값이 존재시 해당 리스트에서 금액 최댓값을 해당 정책의 수혜금액으로 산출
     * 5-2. 년에 해당하는 리스트에 값이 존재시 해당 리스트에서 금액 최댓값을 해당 정책의 수혜금액으로 산출
     * 5-3. 원(금액만 존재)에 해당하는 리스트에 값이 존재시 해당 리스트에서 금액 최댓값을 해당 정책의 수혜금액으로 산출
     */
    public String[] predict(String text) {
        List<String> sents = removeStar(text);

        Pattern moneyPattern = Pattern.compile("([0-9,]+)((천|백|십)?만원|억원|천원|원)");

        List<String[]> moneyInfo = getMoneyInfo(sents, moneyPattern);

        Set<String> finalInfo = new HashSet<>();

        List<String[]> finalDang = new ArrayList<>();
        List<String[]> finalWol = new ArrayList<>();
        List<String[]> finalNyeon = new ArrayList<>();
        List<String[]> finalWon = new ArrayList<>();

        for (String[] info : moneyInfo) {
            String money = info[0];
            String searchSent = info[1];
            String found = "";

            String foundDang = findDang(searchSent);
            if (!foundDang.isEmpty()) {
                found = foundDang;
            } else {
                String foundWol = findWol(searchSent);
                if (!foundWol.isEmpty()) {
                    found = foundWol;
                } else {
                    String foundNyeon = findNyeon(searchSent);
                    if (!foundNyeon.isEmpty()) {
                        found = foundNyeon;
                    }
                }
            }

            if (!found.isEmpty()) {
                List<String> remainFound = Arrays.asList("세대당", "팀당", "월", "매월", "1월", "1개월", "연간", "년간", "년", "1연간", "1년간", "1년");
                List<String> threeFound = Collections.singletonList("분기당");
                List<String> sixFound = Arrays.asList("반기당", "학기당");

                if (remainFound.contains(found) && findNumeral(found) == 0) {
                    money = changeMoneyForm(money, 1); // divide by 1 for simplicity in this context
                } else if (threeFound.contains(found)) {
                    money = changeMoneyForm(money, 3);
                } else if (sixFound.contains(found)) {
                    money = changeMoneyForm(money, 6);
                } else {
                    long numeral = findNumeral(found);
                    money = changeMoneyForm(money, numeral > 0 ? numeral : 1);
                }
            }

            if (found.contains("당")) {
                finalDang.add(new String[]{money, found});
            }
            if (found.contains("월")) {
                finalWol.add(new String[]{money, "월"});
            }
            if (found.contains("연")) {
                finalNyeon.add(new String[]{money, "얀"});
            }
            finalWon.add(new String[]{parseAmount(money), "원"});
        }

        if (finalDang.size() > 0) {
            return finalDang.stream()
                    .max(Comparator.comparingLong(info -> Long.parseLong(info[0])))
                    .orElseThrow(() -> new BusinessException(NOT_FOUND_MONEY_VALUE));
        }

        if (finalWol.size() > 0) {
            return finalWol.stream()
                    .max(Comparator.comparingLong(info -> Long.parseLong(info[0])))
                    .orElseThrow(() -> new BusinessException(NOT_FOUND_MONEY_VALUE));
        }

        if (finalNyeon.size() > 0) {
            return finalNyeon.stream()
                    .max(Comparator.comparingLong(info -> Long.parseLong(info[0])))
                    .orElseThrow(() -> new BusinessException(NOT_FOUND_MONEY_VALUE));
        }

        if (finalWon.size() > 0) {
            return finalWon.stream()
                    .max(Comparator.comparingLong(info -> Long.parseLong(info[0])))
                    .orElseThrow(() -> new BusinessException(NOT_FOUND_MONEY_VALUE));
        }

        return new String[]{"", ""};
    }

    // '*'이 포함된 문장을 제거합니다.
    private List<String> removeStar(String text) {
        List<String> filteredSents = new ArrayList<>();
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (!line.contains("*") && !line.isEmpty()) {
                filteredSents.add(line);
            }
        }
        return filteredSents;
    }

    // 모든 문장에서 금액 정보를 추출합니다.
    private List<String[]> getMoneyInfo(List<String> sents, Pattern moneyPattern) {
        List<String[]> moneyInfo = new ArrayList<>();
        for (String sent : sents) {
            findMoney(sent, moneyPattern).stream()
                    .filter(info -> !info[0].isEmpty())
                    .forEach(moneyInfo::add);
        }
        return moneyInfo;
    }

    // 문장에서 금액 정보를 찾습니다. => [ 금액 , 금액의 첫번쨰 문자 인덱스 - 5 까지의 문자 ]
    private List<String[]> findMoney(String sent, Pattern moneyPattern) {
        List<String[]> results = new ArrayList<>();
        Matcher matcher = moneyPattern.matcher(sent);
        while (matcher.find()) {
            String found = matcher.group();
            int foundIdx = matcher.start();
            int searchIdx = Math.max(foundIdx - 5, 0);
            String searchSent = sent.substring(searchIdx, foundIdx).trim();
            results.add(new String[]{found, searchSent});
        }
        return results;
    }

    private String findDang(String sent) {
        List<String> dangList = Arrays.asList("세대당", "팀당", "분기당", "반기당", "학기당", "기업당");

        return dangList.stream()
                .filter(dang -> sent.contains(dang))
                .findFirst()
                .orElse("");
    }

    // '월'이 들어간 단어를 찾습니다.
    private String findWol(String sent) {
        // 첫 번째 조건을 스트림으로 처리
        String firstMatch = Stream.of("월", "개월")
                .flatMap(wol -> checkNumWord(sent, wol).stream())
                .findFirst()
                .orElse(null); // 첫 번째 매칭된 결과 반환, 없으면 null

        if (firstMatch != null) {
            return firstMatch;
        }

        // 두 번째 조건을 스트림으로 처리
        return Stream.of("월", "매월")
                .filter(wol -> sent.contains(wol))
                .findFirst()
                .orElse(""); // 첫 번째로 찾은 단어 반환, 없으면 빈 문자열
    }

    // '년'이 들어간 단어를 찾습니다.
    private String findNyeon(String sent) {

        String[] nyeons = {"연간", "년간", "년"};

        // 숫자와 함께 나타나는 경우를 우선적으로 검색
        String firstMatch = Arrays.stream(nyeons)
                .flatMap(nyeon -> checkNumWord(sent, nyeon).stream())
                .findFirst()
                .orElse(null); // 첫 번째 매칭된 결과 반환, 없으면 null

        if (firstMatch != null) {
            return firstMatch;
        }

        // 숫자 없이 단어만 나타나는 경우를 검색
        return Arrays.stream(nyeons)
                .filter(nyeon -> sent.contains(nyeon))
                .findFirst()
                .orElse(""); // 첫 번째로 찾은 단어 반환, 없으면 빈 문자열
    }

    // 숫자를 찾고 형식을 변환합니다.
    // 숫자와 함께 있는 특정 단어가 있는지 확인합니다.
    private List<String> checkNumWord(String sent, String word) {
        return Pattern.compile("[0-9]+" + word)
                .matcher(sent)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList()); // 모든 매칭 결과를 리스트로 수집
    }

    private long findNumeral(String text) {
        Pattern numPattern = Pattern.compile("[0-9]+");
        Matcher matcher = numPattern.matcher(text);
        if (matcher.find()) {
            return Long.parseLong(matcher.group());
        }
        return 0;
    }

    private String changeMoneyForm(String money, long divide) {
        money = money.replaceAll(",", "");
        long numeral = Long.parseLong(parseAmount(money));

        if (divide != 0) {
            numeral /= divide;
        }

        return String.valueOf(numeral);
    }

    private String parseAmount(String amountStr) {
        String[] parts = amountStr.split("[천백만억]");
        long num = Long.parseLong(parts[0].replaceAll("[^\\d]", "")); // 숫자 추출 후 숫자로 변환

        if (amountStr.contains("억")) {
            num *= 100000000;
        } else if (amountStr.contains("천만")) {
            num *= 10000000;
        } else if (amountStr.contains("백만")) {
            num *= 1000000;
        } else if (amountStr.contains("십만")) {
            num *= 100000;
        } else if (amountStr.contains("만")) {
            num *= 10000;
        } else if (amountStr.contains("천")) {
            num *= 1000;
        } else if (amountStr.contains("백")) {
            num *= 100;
        } else if (amountStr.contains("만")) {
            num *= 10000;
        }

        return Long.toString(num);
    }

}
