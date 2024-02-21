package com.cmc.zenefitserver.batch.chunk;

import com.cmc.zenefitserver.batch.service.PolicySupportContentClassifier;
import com.cmc.zenefitserver.batch.service.PolicyLoanClassifier;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class PolicyClassifySupportJobConfig {

    private static final String JOB_NAME = "PolicyClassifySupportJob";
    private static final String STEP_NAME = "PolicyClassifySupportJob";
    private static final int CHUNK_SIZE = 100;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory entityManagerFactory;

    private final PolicySupportContentClassifier policySupportContentClassifier;

    private final PolicyLoanClassifier policyLoanClassifier;

    private final PolicyRepository policyRepository;

//    @Bean
    public Job policyClassifySupportJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(policyClassifySupportStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step policyClassifySupportStep() {
        return stepBuilderFactory.get(STEP_NAME)
                .<Policy, Policy>chunk(CHUNK_SIZE)
                .reader(policyRepositoryItemReader(policyRepository))
                .processor(policySupportItemProcessor())
                .writer(policyPagingSupportItemWriter())
                .build();
    }

    @Bean
    public PolicySupportItemProcessor policySupportItemProcessor() {
        return new PolicySupportItemProcessor(policySupportContentClassifier, policyLoanClassifier);
    }


    @Bean
    public JpaItemWriter<Policy> policyPagingSupportItemWriter() {
        JpaItemWriter<Policy> policyJpaItemWriter = new JpaItemWriter<>();
        policyJpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return policyJpaItemWriter;
    }

    @Bean
    public RepositoryItemReader<Policy> policyRepositoryItemReader(PolicyRepository policyRepository) {
        RepositoryItemReader<Policy> reader = new RepositoryItemReader<>();
        reader.setRepository(policyRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(CHUNK_SIZE);

        // 정렬 방식 설정 (예: ID 기준 오름차순 정렬)
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        reader.setSort(sorts);

        return reader;
    }
}
