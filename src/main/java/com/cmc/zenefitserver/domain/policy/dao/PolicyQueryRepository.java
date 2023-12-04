package com.cmc.zenefitserver.domain.policy.dao;

import static com.cmc.zenefitserver.domain.policy.domain.QPolicy.policy;
import static com.cmc.zenefitserver.domain.userpolicy.domain.QUserPolicy.userPolicy;

import com.cmc.zenefitserver.domain.policy.domain.enums.PolicyCode;
import com.cmc.zenefitserver.domain.policy.domain.enums.SupportPolicyType;
import com.cmc.zenefitserver.domain.policy.dto.PolicyListResponseDto;
import com.cmc.zenefitserver.domain.user.domain.User;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PolicyQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    // 유저가 신청할 수 있는 지원 유형별(현금, 대출, 사회서비스)로 정책 찾기
    public Page<PolicyListResponseDto> searchByPaging(User user, SupportPolicyType supportPolicyType, PolicyCode policyCode, String keyword, Pageable pageable) {
        JPAQuery<PolicyListResponseDto> query = jpaQueryFactory.select(
                        Projections.constructor(PolicyListResponseDto.class,
                                policy.id,
                                policy.policyName,
                                policy.policyLogo,
                                policy.policyIntroduction,
                                policy.areaCode.stringValue(),
                                policy.cityCode.stringValue(),
                                policy.policyDateType,
                                policy.benefit,
                                ExpressionUtils.as(Expressions.constant(false), "applyFlag"),
                                ExpressionUtils.as(Expressions.constant(false), "interestFlag")
                        )
                )
                .from(policy)
                .leftJoin(userPolicy)
                .on(policy.id.eq(userPolicy.policy.id).and(userPolicy.user.userId.eq(user.getUserId())))
                .where(
//                        getPolicyId(lastPolicyId, pageable),
                        // 조건
                        policy.supportPolicyType.eq(supportPolicyType)
                );

        if (PolicyCode.NONE != policyCode) {
            query.where(policy.policyCode.eq(policyCode));
        }

        if (!StringUtils.isEmpty(keyword)) {
            query.where(policy.policyName.containsIgnoreCase(keyword));
        }

        List<PolicyListResponseDto> results = query.orderBy(policySort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return PageableExecutionUtils.getPage(results, pageable, query::fetchCount);
    }
//    public Page<PolicyListResponseDto.PolicyInfo> searchByPaging(User user, SupportPolicyType supportPolicyType, PolicyCode policyCode, Pageable pageable) {
//
//
//        EnumPath<PolicySplzType> policySplzType = Expressions.enumPath(PolicySplzType.class,"policySplzType");
//
//        JPAQuery<Policy> query = jpaQueryFactory
//                .selectFrom(policy)
//                .leftJoin(userPolicy)
//                .on(policy.id.eq(userPolicy.policy.id).and(userPolicy.user.userId.eq(user.getUserId())))
//                .where(
//                        policy.supportPolicyType.eq(supportPolicyType)
//                );
//
//        if (PolicyCode.NONE != policyCode) {
//            query.where(policy.policyCode.eq(policyCode));
//        }
//
//        long totalCount = query.fetchCount();
//
////        List<PolicyListResponseDto.PolicyInfo> results =
//                query.
//                orderBy(policySort(pageable))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        List<PolicyListResponseDto.PolicyInfo> results = query
//                .leftJoin(policy.policySplzTypes, policySplzType)
//                .transform(
//                        groupBy(policy.id).list(
//                                Projections.constructor(PolicyListResponseDto.PolicyInfo.class,
//                                        policy.id,
//                                        policy.policyName,
//                                        policy.policyLogo,
//                                        policy.policyIntroduction,
//                                        policy.areaCode.stringValue(),
//                                        policy.cityCode.stringValue(),
//                                        policy.policyDateType,
//                                        policy.benefit,
//                                        ExpressionUtils.as(Expressions.constant(false), "applyFlag"),
//                                        ExpressionUtils.as(Expressions.constant(false), "interestFlag"),
//                                        policy.applySttDate,
//                                        policy.applyEndDate,
//                                        policy.policySplzTypes,
//                                        policy.minAge,
//                                        policy.maxAge,
//                                        GroupBy.set(policySplzType)
//                                )
//                        )
//                );
//
//        return new PageImpl<>(results, pageable, totalCount);
//    }

//    public Page<PolicyListResponseDto> searchBySlice(User user, SupportPolicyType supportPolicyType, PolicyCode policyCode, String keyword, Pageable pageable) {
//        JPAQuery<PolicyListResponseDto> query = jpaQueryFactory.select(
//                        Projections.constructor(PolicyListResponseDto.class,
//                                policy.id,
//                                policy.policyName,
//                                policy.policyApplyDenialReason,
//                                policy.areaCode.stringValue(),
//                                policy.cityCode.stringValue(),
//                                policy.policyLogo,
//                                policy.policyIntroduction,
//                                policy.applyStatus,
//                                policy.benefit,
//                                ExpressionUtils.as(Expressions.constant(false), "applyFlag"),
//                                ExpressionUtils.as(Expressions.constant(false), "interestFlag")
//                        )
//                )
//                .from(policy)
//                .leftJoin(userPolicy)
//                .on(policy.id.eq(userPolicy.policy.id).and(userPolicy.user.userId.eq(user.getUserId())))
//                .where(
////                        getPolicyId(lastPolicyId, pageable),
//                        // 조건
//                        policy.supportPolicyType.eq(supportPolicyType)
//                );
//
//        if (PolicyCode.NONE != policyCode) {
//            query.where(policy.policyCode.eq(policyCode));
//        }
//
//        if (keyword != null && !keyword.isBlank()) {
//            query.where(policy.policyName.containsIgnoreCase(keyword));
//        }
//
//        long totalCount = query.fetchCount();
//        List<PolicyListResponseDto> results = query.orderBy(policySort(pageable))
//                .limit(pageable.getPageSize() + 1)
//                .fetch();
//
//        return new PageImpl<>(results, pageable, totalCount);
//    }

    //    private BooleanExpression ltPolicyId(Long policyId) {
//    public Slice<PolicyListResponseDto> searchBySlice(User user, Long lastPolicyId, SupportPolicyType supportPolicyType, PolicyCode policyCode, String keyword, Pageable pageable) {
//        JPAQuery<PolicyListResponseDto> query = jpaQueryFactory.select(
//                        Projections.constructor(PolicyListResponseDto.class,
//                                policy.id,
//                                policy.policyName,
//                                policy.policyApplyDenialReason,
//                                policy.areaCode.stringValue(),
//                                policy.cityCode.stringValue(),
//                                policy.policyLogo,
//                                policy.policyIntroduction,
//                                policy.applyStatus,
//                                policy.benefit,
//                                ExpressionUtils.as(Expressions.constant(false), "applyFlag"),
//                                ExpressionUtils.as(Expressions.constant(false), "interestFlag")
//                        )
//                )
//                .from(policy)
//                .leftJoin(userPolicy)
//                .on(policy.id.eq(userPolicy.policy.id).and(userPolicy.user.userId.eq(user.getUserId())))
//                .where(
////                        getPolicyId(lastPolicyId, pageable),
//                        // 조건
//                        policy.supportPolicyType.eq(supportPolicyType)
//                );
//
//        if (PolicyCode.NONE != policyCode) {
//            query.where(policy.policyCode.eq(policyCode));
//        }
//
//        if (keyword != null && !keyword.isBlank()) {
//            query.where(policy.policyName.containsIgnoreCase(keyword));
//        }
//        List<PolicyListResponseDto> results = query.orderBy(policySort(pageable))
//                .limit(pageable.getPageSize() + 1)
//                .fetch();
//        return checkLastPage(pageable, results);
//    }
    private OrderSpecifier<?> policySort(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort != null) {
            for (Sort.Order order : sort) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "benefit":
                        return new OrderSpecifier(direction, policy.benefit);
                    case "applyEndDate":
                        return new OrderSpecifier(direction, policy.applyEndDate);
                }
            }
        }
        return new OrderSpecifier(Order.ASC, policy.id); // 기본이 policy ID 오름차순
    }

//        if (policyId == null) {
//            return null;
//        }
//
//        return policy.id.gt(policyId);
//    }

//    private BooleanExpression getPolicyId(Long policyId, Pageable pageable) {
//        if (policyId == null) {
//            return null;
//        }
//        Sort sort = pageable.getSort();
//        if (sort != null) {
//            for (Sort.Order order : sort) {
//                boolean isAscending = order.getDirection().isAscending();
//                if (isAscending) {
//                    return policy.id.gt(policyId);
//                }
//                if (!isAscending) {
//                    return policy.id.lt(policyId);
//                }
//            }
//        }
//        return policy.id.gt(policyId); // 기본이 policy ID 오름차순이기 때문에,
//    }

    // 무한 스크롤 방식 처리하는 메서드
//    private Slice<PolicyListResponseDto> checkLastPage(Pageable pageable, List<PolicyListResponseDto> results) {
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
