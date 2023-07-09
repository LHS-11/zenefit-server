package com.cmc.zenefitserver.domain.user.domain;

import com.cmc.zenefitserver.global.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="user_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetail extends BaseEntity {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @MapsId
    @JsonIgnore
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    // 중소기업
    private boolean isSmallBusiness;

    // 군인
    private boolean isSoldier;

    // 저소득층
    private boolean isLowIncome;

    // 장애인
    private boolean isDisabled;

    // 지역 인재
    private boolean isLocalTalent;

    // 농업인
    private boolean isFarmer;

    // 제한 없음
    private boolean isNoLimit;


}
