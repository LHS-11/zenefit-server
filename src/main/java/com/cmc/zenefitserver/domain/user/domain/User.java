package com.cmc.zenefitserver.domain.user.domain;


import com.cmc.zenefitserver.domain.job.domain.Job;
import com.cmc.zenefitserver.global.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name="nickname",unique = true)
    @NotNull
    private String nickname;

    @Column(name = "age")
    @NotNull
    private Integer age;

    @Embedded
    private Address address;

    @Column(name = "last_year_income")
    @NotNull
    private int lastYearIncome;

    @Column(name = "education_type")
    @Enumerated(EnumType.STRING)
    private EducationType educationType; // 학력 요건 내용

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @ToString.Exclude
    private Set<Job> jobs = new HashSet<>();

    @Column(name = "policy_cnt")
    private int policyCnt;

    @OneToOne(mappedBy = "user")
    @JoinColumn(name = "user_detail")
    private UserDetail userDetail;

    // 정책

    @Column(name = "fcm_token")
    private String fcmToken;

    // 알림 관련
    @Column(name = "push_notification_status")
    private boolean pushNotificationStatus;

    @Column(name = "app_notification_status")
    private boolean appNotificationStatus;


}
