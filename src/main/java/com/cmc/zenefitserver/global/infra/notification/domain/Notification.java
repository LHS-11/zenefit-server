package com.cmc.zenefitserver.global.infra.notification.domain;

import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.enums.SearchDateType;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "zenefit_notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "NOTIFICATION_SEQ_GENERATOR",
        sequenceName = "notification_seq",
        initialValue = 1,
        allocationSize = 1
)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long policyId;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean readFlag;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String image;

    @Enumerated(EnumType.STRING)
    private SearchDateType searchDateType;

    @Builder
    public Notification(User user, Long policyId, boolean readFlag, String title, String content, String image, SearchDateType searchDateType) {
        this.user = user;
        this.policyId = policyId;
        this.readFlag = readFlag;
        this.title = title;
        this.content = content;
        this.image = image;
        this.searchDateType = searchDateType;
    }
}
