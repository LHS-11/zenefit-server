package com.cmc.zenefitserver.batch.chunk;

import com.cmc.zenefitserver.batch.service.*;
import com.cmc.zenefitserver.domain.policy.application.*;
import com.cmc.zenefitserver.domain.policy.dao.PolicyRepository;
import com.cmc.zenefitserver.domain.policy.domain.Policy;
import com.cmc.zenefitserver.domain.policy.domain.YouthPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class PolicyCreateJobConfig {

    private static final String JOB_NAME = "PolicyCreateJob";
    private static final String STEP_NAME = "PolicyCreateStep";
    private static final int CHUNK_SIZE = 500;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory entityManagerFactory;

    private final YouthPolicyService youthPolicyService;

    private final PolicyAgeClassifier policyAgeClassifier;

    private final PolicyEmpmClassifier policyEmpmClassifier;

    private final PolicyEduClassifier policyEduClassifier;

    private final PolicySplzClassifier policySplzClassifier;

    private final PolicyMethodClassifier policyMethodClassifier;

    private final PolicyRepository policyRepository;

    /**
     * Job 1 - 완료
     * 1.	정책 xml 가져오기
     * 2.	나이 분류
     * 3.	지역 코드 분류
     * 4.	직업 타입 분류 ( 재직자, 프리랜서 등)
     * 5.	교육 타입 분류 ( 대학생, 고졸미만 등 )
     * 6.	스페셜 타입 분류 ( 여성, 군인 등)
     * 7.	정책 저장
     * <p>
     * Job 2
     * 1.	날짜 타입 분류
     * 2.	신청 방법 분류
     * <p>
     * Job 3
     * 1.	파이썬 flask 랑 연동해서 정책 지원 유형 분류하기
     * 2.	수혜 금액 분류하기
     */

//    @Bean
    public Job policyCreateJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(policyCreateStep())
//                .next(policyClassifyDateJob())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step policyCreateStep() {
        return stepBuilderFactory.get(STEP_NAME)
                .<YouthPolicy, Policy>chunk(CHUNK_SIZE)
                .reader(youthPolicyPagingItemReader())
                .processor(youthPolicyItemProcessor())
                .writer(policyItemWriter())
                .build();
    }


    @Bean
    public YouthPolicyPagingItemReader youthPolicyPagingItemReader() {
        YouthPolicyPagingItemReader itemReader = new YouthPolicyPagingItemReader(youthPolicyService);
        itemReader.setPageSize(CHUNK_SIZE);
        return itemReader;
    }

    @Bean
    public YouthPolicyItemProcessor youthPolicyItemProcessor() {
        return new YouthPolicyItemProcessor(policyAgeClassifier, policyEmpmClassifier, policyEduClassifier, policySplzClassifier, policyMethodClassifier, policyRepository);
    }

    @Bean
    public JpaItemWriter<Policy> policyItemWriter() {
        JpaItemWriter<Policy> policyJpaItemWriter = new JpaItemWriter<>();
        policyJpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return policyJpaItemWriter;
    }

}

