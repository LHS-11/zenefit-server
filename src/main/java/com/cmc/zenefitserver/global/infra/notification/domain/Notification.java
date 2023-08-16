package com.cmc.zenefitserver.global.infra.notification.domain;

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
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean readFlag;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String image;

    @Builder
    public Notification(User user, boolean readFlag, String title, String content,String image) {
        this.user = user;
        this.readFlag = readFlag;
        this.title = title;
        this.content = content;
        this.image = image;
    }
}
