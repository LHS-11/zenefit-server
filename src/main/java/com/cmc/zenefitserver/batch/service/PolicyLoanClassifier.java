package com.cmc.zenefitserver.batch.service;

import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class PolicyLoanClassifier {

    public void saveBenefit(Policy policy) {

        Set<SupportPolicyType> supportPolicyTypes = policy.getSupportPolicyTypes();

        if (supportPolicyTypes != null && supportPolicyTypes.contains(SupportPolicyType.LOANS)) {
            long benefit = predict(policy.getSupportContent());
            policy.updateBenefit(BigDecimal.valueOf(benefit));
        }
    }

    public long predict(String text) {

        ArrayList<String> sentences = decompose(text);
        long maxMoney = 0;

        for (String sentence : sentences) {
            ArrayList<String> moneyStrings = findAllMoney(sentence);
            ArrayList<Long> moneyValues = transposeAllMoney(moneyStrings);

            for (Long money : moneyValues) {
                if (money > maxMoney) {
                    maxMoney = money;
                }
            }
        }

        return maxMoney;
    }

    public static ArrayList<String> decompose(String text) {
        String[] lines = text.split("\n");
        ArrayList<String> sentences = new ArrayList<>();
        for (String line : lines) {
            if (!line.isEmpty()) {
                sentences.add(line);
            }
        }
        return sentences;
    }

    public static ArrayList<String> findAllMoney(String sentence) {
        Pattern[] moneyPatterns = new Pattern[]{
                Pattern.compile("[0-9,]+억원"),
                Pattern.compile("[0-9,]+천만원"),
                Pattern.compile("[0-9,]+백만원"),
                Pattern.compile("[0-9,]+십만원"),
                Pattern.compile("[0-9,]+만원"),
                Pattern.compile("[0-9,]+천원"),
                Pattern.compile("[0-9,]+원")
        };

        ArrayList<String> founds = new ArrayList<>();
        for (Pattern moneyPattern : moneyPatterns) {
            founds.addAll(findMoney(sentence, moneyPattern));
        }

        for (int i = 0; i < founds.size(); i++) {
            founds.set(i, founds.get(i).replaceAll(",", ""));
        }

        return founds;
    }

    public static ArrayList<String> findMoney(String sentence, Pattern moneyPattern) {
        ArrayList<String> found = new ArrayList<>();
        Matcher matcher = moneyPattern.matcher(sentence);
        while (matcher.find()) {
            found.add(matcher.group());
        }
        return found;
    }

    public static ArrayList<Long> transposeAllMoney(ArrayList<String> moneys) {
        ArrayList<Long> transposed = new ArrayList<>();
        for (String money : moneys) {
            int numeral = findNumeral(money);

            if (money.contains("억원")) {
                transposed.add((long) numeral * 100000000);
            } else if (money.contains("천만원")) {
                transposed.add((long) numeral * 10000000);
            } else if (money.contains("백만원")) {
                transposed.add((long) numeral * 1000000);
            } else if (money.contains("십만원")) {
                transposed.add((long) numeral * 100000);
            } else if (money.contains("만원")) {
                transposed.add((long) numeral * 10000);
            } else if (money.contains("천원")) {
                transposed.add((long) numeral * 1000);
            } else {  // "원"
                transposed.add((long) numeral);
            }
        }
        return transposed;
    }

    public static int findNumeral(String text) {
        try {
            Pattern numPattern = Pattern.compile("[0-9]+");
            Matcher matcher = numPattern.matcher(text);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group());
            }
        } catch (NumberFormatException e) {
            return 0;
        }
        return 0;
    }


//    public static void main(String[] args) {
//        String text = "지역대학입학장학금 1인당 2백만원 250명\n" +
//                "기업연계취업장학금 1인당 3.6백만원";
//        long maxMoney = predict(text);
//        System.out.println("최대 금액: " + maxMoney + "원");
//    }
}