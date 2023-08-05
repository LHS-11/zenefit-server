package com.cmc.zenefitserver.domain.policy.dao;

import static com.cmc.zenefitserver.domain.policy.domain.QPolicy.policy;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PolicyQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    public Slice<Policy> searchBySlice(Long lastPolicyId, Pageable pageable){
        List<Policy> results = jpaQueryFactory.selectFrom(policy)
                .where(
                        ltPolicyId(lastPolicyId)
                        // 조건
                )
                .orderBy(policy.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, results);
    }

    private BooleanExpression ltPolicyId(Long policyId) {
        if (policyId == null) {
            return null;
        }
        return policy.id.lt(policyId);
    }

    // 무한 스크롤 방식 처리하는 메서드
    private Slice<Policy> checkLastPage(Pageable pageable, List<Policy> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
