package com.cmc.zenefitserver.domain.feed.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToMany(mappedBy = "feed", fetch = FetchType.LAZY)
    private Set<Tag> tags;

    private String picture;

    private String title; // 제목
    private String text; // 본문

    private String bizId; // policyId




}
