package com.cmc.zenefitserver.domain.policy.domain.enums;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public enum CashBenefitType {

    DANG("당"),
    WOL("월"),
    NYEON("년");

    final String name;


    public static String findCashBenefit(String text){
        CashBenefitType cashBenefitType = findCashBenefitType(text);

        if(cashBenefitType != null && !cashBenefitType.equals(DANG)){
            return cashBenefitType.name;
        }

        return null;
    }

    public static CashBenefitType findCashBenefitType(String text){
        return Arrays.stream(CashBenefitType.values())
                .filter(cashBenefitType -> text != null && text.contains(cashBenefitType.name))
                .findFirst()
                .orElse(null);
    }

}
