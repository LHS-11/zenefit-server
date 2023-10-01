package com.cmc.zenefitserver.domain.user.domain;

import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Embeddable
public class Address {

    @Column(name = "city")
    @Enumerated(EnumType.STRING)
    private AreaCode areaCode;

    @Column(name = "district")
    @Enumerated(EnumType.STRING)
    private CityCode cityCode;

    @Builder
    public Address(AreaCode areaCode, CityCode cityCode) {
        this.areaCode = areaCode;
        this.cityCode = cityCode;
    }
}
