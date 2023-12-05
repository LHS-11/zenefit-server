package com.cmc.zenefitserver.domain.userpolicy.domain;

import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.common.BaseEntity;
import lombok.*;

import javax.persistence.*;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;

    private boolean applyFlag;

    private boolean interestFlag;

    @Enumerated(EnumType.STRING)
    private UserPolicyType userPolicyType;

    @Builder
    public UserPolicy(User user, Policy policy, boolean applyFlag, boolean interestFlag, UserPolicyType userPolicyType) {
        this.user = user;
        this.policy = policy;
        this.applyFlag = applyFlag;
        this.interestFlag = interestFlag;
        this.userPolicyType = userPolicyType;
    }

    public void updateUserPolicy(User user, Policy policy) {
        updateUser(user);
        updatePolicy(policy);
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public void updatePolicy(Policy policy) {
        this.policy = policy;
    }

    public void updatePolicyType(UserPolicyType userPolicyType) {
        this.userPolicyType = userPolicyType;
    }


    public void setInterestFlagToTrue() {
        if (!this.interestFlag) {
            this.interestFlag = true;
        }
    }

    public void setInterestFlagToFalse() {
        if (this.interestFlag) {
            this.interestFlag = false;
        }
    }

    public void setApplyFlagToTrue() {
        if (!this.applyFlag) {
            this.applyFlag = true;
        }
    }

    public void setApplyFlagToFalse() {
        if (this.applyFlag) {
            this.applyFlag = false;
        }
    }
}
