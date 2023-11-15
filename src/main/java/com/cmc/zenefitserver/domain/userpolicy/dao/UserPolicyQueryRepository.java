package com.cmc.zenefitserver.domain.userpolicy.dao;

import com.cmc.zenefitserver.domain.user.domain.User;
import com.cmc.zenefitserver.domain.userpolicy.dto.InterestAndApplyPolicyListResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmc.zenefitserver.domain.policy.domain.QPolicy.policy;
import static com.cmc.zenefitserver.domain.userpolicy.domain.QUserPolicy.userPolicy;

@RequiredArgsConstructor
@Repository
public class UserPolicyQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;


    public Slice<InterestAndApplyPolicyListResponseDto> searchInterestPolicyBySlice(User user, Long lastPolicyId, Pageable pageable) {
        List<InterestAndApplyPolicyListResponseDto> results = jpaQueryFactory.select(
                        Projections.constructor(InterestAndApplyPolicyListResponseDto.class,
                                userPolicy.policy.id,
                                userPolicy.policy.policyName,
                                userPolicy.policy.policyIntroduction,
                                userPolicy.policy.applyEndDate,
                                userPolicy.policy.policyLogo
                        )
                )
                .from(userPolicy)
                .where(
                        ltPolicyId(lastPolicyId),
                        userPolicy.user.userId.eq(user.getUserId()),
                        userPolicy.interestFlag.eq(true)
                )
                .orderBy(userPolicy.policy.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, results);
    }

//    public Slice<ApplyPolicyListResponseDto> searchApplyPolicyBySlice(User user, Long lastPolicyId, Pageable pageable) {
//        List<ApplyPolicyListResponseDto> results = jpaQueryFactory.select(
//                        Projections.constructor(ApplyPolicyListResponseDto.class,
//                                userPolicy.policy.id,
//                                userPolicy.policy.policyName,
//                                userPolicy.policy.policyIntroduction,
//                                userPolicy.policy.benefit,
//                                userPolicy.policy.policyLogo
//                        )
//                )
//                .from(userPolicy)
//                .where(
//                        ltPolicyId(lastPolicyId),
//                        userPolicy.user.userId.eq(user.getUserId()),
//                        userPolicy.applyFlag.eq(true)
//                )
//                .orderBy(userPolicy.policy.id.desc())
//                .limit(pageable.getPageSize() + 1)
//                .fetch();
//
//        return checkLastPageApply(pageable, results);
//    }

    private BooleanExpression ltPolicyId(Long policyId) {
        if (policyId == null) {
            return null;
        }
        return policy.id.lt(policyId);
    }

    // 무한 스크롤 방식 처리하는 메서드
    private Slice<InterestAndApplyPolicyListResponseDto> checkLastPage(Pageable pageable, List<InterestAndApplyPolicyListResponseDto> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

//    private Slice<ApplyPolicyListResponseDto> checkLastPageApply(Pageable pageable, List<ApplyPolicyListResponseDto> results) {
//
//        boolean hasNext = false;
//
//        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
//        if (results.size() > pageable.getPageSize()) {
//            hasNext = true;
//            results.remove(pageable.getPageSize());
//        }
//
//        return new SliceImpl<>(results, pageable, hasNext);
//    }
}
