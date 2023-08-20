package com.cmc.zenefitserver.domain.user.domain;

import com.cmc.zenefitserver.domain.policy.domain.enums.AreaCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.CityCode;
import io.swagger.annotations.ApiModelProperty;
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

    @NotNull
    @Column(name = "city")
    @Enumerated(EnumType.STRING)
    private AreaCode city;

    @NotNull
    @Column(name = "district")
    @Enumerated(EnumType.STRING)
    private CityCode district;

    @Builder
    public Address(AreaCode city, CityCode district) {
        this.city = city;
        this.district = district;
    }
}
