package com.cmc.zenefitserver.domain.user.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Embeddable
public class Address {

    @NotNull
    @ApiModelProperty(notes = "도시", example = "서울시")
    @Column(name = "city")
    private String city;

    @NotNull
    @ApiModelProperty(notes = "구/군", example = "강서구")
    @Column(name = "district")
    private String district;

    @Builder
    public Address(String city, String district) {
        this.city = city;
        this.district = district;
    }
}
