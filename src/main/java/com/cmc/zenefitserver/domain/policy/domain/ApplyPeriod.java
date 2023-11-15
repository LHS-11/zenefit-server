package com.cmc.zenefitserver.domain.policy.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ApplyPeriod implements Comparable<ApplyPeriod>{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private LocalDate sttDate;

    private LocalDate endDate;

    @Builder
    public ApplyPeriod(LocalDate sttDate, LocalDate endDate) {
        this.sttDate = sttDate;
        this.endDate = endDate;
    }

    @Override
    public int compareTo(ApplyPeriod o) {
        return this.endDate.compareTo(o.endDate);
    }

//    @Override
//    public int compareTo(ApplyPeriod o) {
//        return this.endDate.compareTo(o.endDate);
//    }
}


