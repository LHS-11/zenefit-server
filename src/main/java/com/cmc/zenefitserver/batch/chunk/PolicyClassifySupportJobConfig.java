package com.cmc.zenefitserver.batch.chunk;

import com.cmc.zenefitserver.batch.service.PolicySupportContentClassifier;
import com.cmc.zenefitserver.domain.policy.application.PolicyBenefitClassifier;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class PolicyClassifySupportJobConfig {

    private static final String JOB_NAME = "PolicyClassifySupportJob";
    private static final String STEP_NAME = "PolicyClassifySupportJob";
    private static final int CHUNK_SIZE = 300;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory entityManagerFactory;

    private final PolicySupportContentClassifier policySupportContentClassifier;

    private final PolicyBenefitClassifier policyBenefitClassifier;

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
                .reader(policyPagingItemReader())
                .processor(policySupportItemProcessor())
                .writer(policyPagingSupportItemWriter())
                .build();
    }

    @Bean
    public PolicySupportItemProcessor policySupportItemProcessor() {
        return new PolicySupportItemProcessor(policySupportContentClassifier, policyBenefitClassifier);
    }


    @Bean
    public JpaItemWriter<Policy> policyPagingSupportItemWriter() {
        JpaItemWriter<Policy> policyJpaItemWriter = new JpaItemWriter<>();
        policyJpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return policyJpaItemWriter;
    }

    private ItemReader<? extends Policy> policyPagingItemReader() {
        return new JpaPagingItemReaderBuilder<Policy>()
                .name("JpaPagingPolicyItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select p from Policy p "
                        + "left join fetch p.jobTypes jt "
                        + "left join fetch p.educationTypes et "
                        + "left join fetch p.policySplzTypes pst "
                        + "left join fetch p.supportPolicyTypes spt "
                        + "left join fetch p.userPolicies up "
                        + "left join fetch p.applyPeriods ap")
                .build();
    }

}
