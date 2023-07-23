package com.cmc.zenefitserver.domain.user.domain;


import com.cmc.zenefitserver.domain.user.dto.ModifyRequestDto;
import com.cmc.zenefitserver.global.auth.ProviderType;
import com.cmc.zenefitserver.global.common.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userId;

    @Column(name = "email",unique = true)
    private String email;

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

    @ElementCollection
    @CollectionTable(name = "jobs", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<JobType> jobs;

    @Column(name = "policy_cnt")
    private int policyCnt;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
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

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    public void setUserDetail(UserDetail userDetail){
        this.userDetail = userDetail;
    }
    public void update(ModifyRequestDto modifyRequestDto) {
        this.nickname=modifyRequestDto.getNickname();
        this.age=modifyRequestDto.getAge();
        this.address=modifyRequestDto.getAddress();
        this.lastYearIncome=modifyRequestDto.getLastYearIncome();
        this.educationType=modifyRequestDto.getEducationType();
        this.jobs=modifyRequestDto.getJobs();
        this.userDetail.modify(modifyRequestDto.getUserDetail());
    }


    @Builder
    public User(String email, String nickname, Integer age, Address address, int lastYearIncome, EducationType educationType, Set<JobType> jobs, int policyCnt, UserDetail userDetail, String fcmToken, boolean pushNotificationStatus, boolean appNotificationStatus, ProviderType provider) {
        this.email = email;
        this.nickname = nickname;
        this.age = age;
        this.address = address;
        this.lastYearIncome = lastYearIncome;
        this.educationType = educationType;
        this.jobs = jobs;
        this.policyCnt = policyCnt;
        this.userDetail = userDetail;
        this.fcmToken = fcmToken;
        this.pushNotificationStatus = pushNotificationStatus;
        this.appNotificationStatus = appNotificationStatus;
        this.provider = provider;
    }

}
