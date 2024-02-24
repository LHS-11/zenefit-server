package com.cmc.zenefitserver.domain.policy.domain.enums;


import com.cmc.zenefitserver.domain.policy.domain.Policy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public enum CashBenefitType {

    DANG("당", benefit -> benefit),
    WOL("월", benefit -> benefit.multiply(BigDecimal.valueOf(12))),
    NYEON("년", benefit -> benefit);

    final String name;
    final Function<BigDecimal, BigDecimal> calculator;

    public static String findCashBenefit(Policy policy) {
        CashBenefitType cashBenefitType = findCashBenefitType(policy);

        if (cashBenefitType != null && !cashBenefitType.equals(DANG)) {
            return cashBenefitType.name;
        }

        return null;
    }

    public static CashBenefitType findCashBenefitType(Policy policy) {
        String benefitPeriod = policy.getBenefitPeriod();
        return Arrays.stream(CashBenefitType.values())
                .filter(cashBenefitType -> benefitPeriod != null && benefitPeriod.contains(cashBenefitType.name))
                .findFirst()
                .orElse(null);
    }

    public BigDecimal calculateBenefit(Policy policy) {
        return this.calculator.apply(policy.getBenefit());
    }

}
