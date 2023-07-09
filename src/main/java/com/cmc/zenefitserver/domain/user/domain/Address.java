package com.cmc.zenefitserver.domain.user.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class Address {

    @NotNull
    @Column(name = "city")
    private String city;

    @NotNull
    @Column(name = "district")
    private String district;
}
