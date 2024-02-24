package com.cmc.zenefitserver.domain.policy.dao;

import static com.cmc.zenefitserver.domain.policy.domain.QPolicy.policy;
import static com.cmc.zenefitserver.domain.userpolicy.domain.QUserPolicy.userPolicy;

import com.cmc.zenefitserver.domain.policy.domain.enums.*;
import com.cmc.zenefitserver.domain.policy.dto.response.PolicyListInfoDto;
import com.cmc.zenefitserver.domain.user.domain.*;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PolicyQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    // 유저가 신청할 수 있는 지원 유형별(현금, 대출, 사회서비스)로 정책 찾기
    public Page<PolicyListInfoDto> searchByAppliedPaging(User user, SupportPolicyType supportPolicyType, PolicyCode policyCode, String keyword, Pageable pageable) {


        JPAQuery<PolicyListInfoDto> query = jpaQueryFactory.select(
                        Projections.constructor(PolicyListInfoDto.class,
                                policy.id,
                                policy.policyName,
                                policy.policyLogo,
                                policy.policyIntroduction,
                                policy.areaCode.stringValue(),
                                policy.cityCode.stringValue(),
                                policy.policyDateType,
                                policy.benefit,
                                policy.benefitPeriod,
                                Expressions.cases()
                                        .when(userPolicy.applyFlag.isNull())
                                        .then(false)
                                        .otherwise(userPolicy.applyFlag)
                                        .as("applyFlag"),
                                Expressions.cases()
                                        .when(userPolicy.interestFlag.isNull())
                                        .then(false)
                                        .otherwise(userPolicy.interestFlag)
                                        .as("interestFlag"),
                                policy.applicationProcedureContent.as("policyMethodTypeDescription"),
                                policy.applicationSiteAddress.as("policyUrl")
                        )
                )
                .from(policy)
                .leftJoin(userPolicy)
                .on(policy.id.eq(userPolicy.policy.id).and(userPolicy.user.userId.eq(user.getUserId())))
                .where(
//                        getPolicyId(lastPolicyId, pageable),
                        // 조건
                        policy.supportPolicyTypes.contains(supportPolicyType),
                        // 사용자 조건
                        isAge(user)
                                .and(isLocal(user))
                                .and(isDate(user))
                                .and(isJob(user))
                                .and(isEducation(user))
                                // 직장 정보, 중소기업
                                .and(isSqlzType(user))
                );

        if (PolicyCode.NONE != policyCode) {
            query.where(policy.policyCode.eq(policyCode));
        }

        if (!StringUtils.isEmpty(keyword)) {
            query.where(policy.policyName.containsIgnoreCase(keyword));
        }


        List<PolicyListInfoDto> results = query.orderBy(policySort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        return PageableExecutionUtils.getPage(results, pageable, query::fetchCount);
    }

    private BooleanExpression isDate(User user) {
        LocalDate now = LocalDate.now();
        // 1. 정책 날짜 타입이 null 이 아니고, 기간신청일때,
        return policy.policyDateType.isNotNull().and(policy.policyDateType.ne(PolicyDateType.PERIOD))
                .or(policy.policyDateType.isNotNull().and(policy.policyDateType.eq(PolicyDateType.PERIOD))
                        .and(policy.applyEndDate.isNotNull().and(policy.applyEndDate.after(now)))
                        .and(policy.applySttDate.isNotNull().and(policy.applySttDate.before(now))));
    }

    private BooleanExpression isAge(User user) {
        return policy.minAge.loe(user.getAge()).and(policy.maxAge.goe(user.getAge()));
    }

    private BooleanExpression isLocal(User user) {
        return policy.areaCode.eq(AreaCode.CENTRAL_GOVERNMENT)
                .or(policy.cityCode.isNull().and(policy.areaCode.eq(user.getAddress().getAreaCode())))
                .or(policy.cityCode.isNotNull().and(policy.cityCode.eq(user.getAddress().getCityCode())));
    }

    private BooleanExpression isJob(User user) {
        if (user.getJobs() == null || user.getJobs().isEmpty()) {
            return policy.jobTypes.contains(JobType.UNLIMITED);
        }
        return policy.jobTypes.contains(JobType.UNLIMITED).or(policy.jobTypes.any().in(user.getJobs()));
    }

    private BooleanExpression isEducation(User user) {
        return policy.educationTypes.contains(EducationType.UNLIMITED).or(policy.educationTypes.contains(user.getEducationType()));
    }

    private BooleanExpression isSqlzType(User user) {
        // policy.policySplzTypes.isEmpty() 임시 적용
        BooleanExpression booleanExpression = policy.policySplzTypes.contains(PolicySplzType.UNLIMITED).or(policy.policySplzTypes.isEmpty());
        if (user.getUserDetail().isDisabled()) {
            booleanExpression = booleanExpression.or(policy.policySplzTypes.contains(PolicySplzType.DISABLED));
        }
        if (user.getUserDetail().isSoldier()) {
            booleanExpression = booleanExpression.or(policy.policySplzTypes.contains(PolicySplzType.SOLDIER));
        }
        if (user.getUserDetail().isFarmer()) {
            booleanExpression = booleanExpression.or(policy.policySplzTypes.contains(PolicySplzType.FARMER));
        }
        if (user.getUserDetail().getGender().equals(Gender.FEMALE)) {
            booleanExpression = booleanExpression.or(policy.policySplzTypes.contains(PolicySplzType.FEMALE));
        }
        if (user.getUserDetail().isLocalTalent()) {
            booleanExpression = booleanExpression.or(policy.policySplzTypes.contains(PolicySplzType.LOCAL_TALENT));
        }
        if (user.getUserDetail().isLowIncome()) {
            booleanExpression = booleanExpression.or(policy.policySplzTypes.contains(PolicySplzType.LOW_INCOME));
        }
        if (user.getUserDetail().isSmallBusiness()) {
            booleanExpression = booleanExpression.or(policy.policySplzTypes.contains(PolicySplzType.SMALL_BUSINESS));
        }

        return booleanExpression;
    }

    public Page<PolicyListInfoDto> searchByNonAppliedPaging(User user, SupportPolicyType supportPolicyType, PolicyCode policyCode, String keyword, Pageable pageable, List<Long> appliedPolicyIds) {

        JPAQuery<PolicyListInfoDto> query = jpaQueryFactory.select(
                        Projections.constructor(PolicyListInfoDto.class,
                                policy.id,
                                policy.policyName,
                                policy.policyLogo,
                                policy.policyIntroduction,
                                policy.areaCode.stringValue(),
                                policy.cityCode.stringValue(),
                                policy.policyDateType,
                                policy.benefit,
                                policy.benefitPeriod,
                                Expressions.cases()
                                        .when(userPolicy.applyFlag.isNull())
                                        .then(false)
                                        .otherwise(userPolicy.applyFlag)
                                        .as("applyFlag"),
                                Expressions.cases()
                                        .when(userPolicy.interestFlag.isNull())
                                        .then(false)
                                        .otherwise(userPolicy.interestFlag)
                                        .as("interestFlag"),
                                policy.applicationProcedureContent.as("policyMethodTypeDescription"),
                                policy.applicationSiteAddress.as("policyUrl")
                        )
                )
                .from(policy)
                .leftJoin(userPolicy)
                .on(policy.id.eq(userPolicy.policy.id).and(userPolicy.user.userId.eq(user.getUserId())))
                .where(
                        policy.supportPolicyTypes.contains(supportPolicyType),
                        policy.id.notIn(appliedPolicyIds)
                );

        if (PolicyCode.NONE != policyCode) {
            query.where(policy.policyCode.eq(policyCode));
        }

        if (!StringUtils.isEmpty(keyword)) {
            query.where(policy.policyName.containsIgnoreCase(keyword));
        }

        List<PolicyListInfoDto> results = query.orderBy(policySort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(results, pageable, query::fetchCount);
    }

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

}
