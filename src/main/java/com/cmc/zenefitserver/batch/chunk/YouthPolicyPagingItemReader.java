package com.cmc.zenefitserver.batch.chunk;

import com.cmc.zenefitserver.batch.service.YouthPolicyService;
import com.cmc.zenefitserver.domain.policy.domain.YouthPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class YouthPolicyPagingItemReader extends AbstractPagingItemReader<YouthPolicy> {

    private final YouthPolicyService youthPolicyService;

    @Override
    protected void doReadPage() {

        List<YouthPolicy> youthPolices = youthPolicyService.getYouthPolices(getPage() + 1);

        if (results == null) {
            results = new CopyOnWriteArrayList<>();
        } else {
            results.clear();
        }

        results.addAll(youthPolices);
    }

    @Override
    protected void doJumpToPage(int itemIndex) {

    }
}
