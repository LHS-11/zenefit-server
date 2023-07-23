package com.cmc.zenefitserver.domain.user.domain;

import com.cmc.zenefitserver.global.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@ToString
@Getter
@Entity
@Table(name = "user_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetail extends BaseEntity {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @MapsId
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    // 중소기업
    private boolean smallBusiness;

    // 군인
    private boolean soldier;

    // 저소득층
    private boolean lowIncome;

    // 장애인
    private boolean disabled;

    // 지역 인재
    private boolean localTalent;

    // 농업인
    private boolean farmer;

    public void setUser(User user) {
        this.user = user;
    }

    public void modify(UserDetail userDetail) {
        this.gender = userDetail.getGender();
        this.smallBusiness = userDetail.isSmallBusiness();
        this.soldier = userDetail.isSoldier();
        this.lowIncome = userDetail.isLowIncome();
        this.disabled = userDetail.isDisabled();
        this.localTalent = userDetail.isLocalTalent();
        this.farmer = userDetail.isFarmer();
    }

    @Builder
    public UserDetail(User user, Gender gender, boolean smallBusiness, boolean soldier, boolean lowIncome, boolean disabled, boolean localTalent, boolean farmer) {
        this.user = user;
        this.gender = gender;
        this.smallBusiness = smallBusiness;
        this.soldier = soldier;
        this.lowIncome = lowIncome;
        this.disabled = disabled;
        this.localTalent = localTalent;
        this.farmer = farmer;
    }


}
