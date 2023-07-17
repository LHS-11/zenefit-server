package com.cmc.zenefitserver.domain.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Embeddable
public class Address {

    @NotNull
    @Column(name = "city")
    private String city;

    @NotNull
    @Column(name = "district")
    private String district;

    @Builder
    public Address(String city, String district) {
        this.city = city;
        this.district = district;
    }
}
