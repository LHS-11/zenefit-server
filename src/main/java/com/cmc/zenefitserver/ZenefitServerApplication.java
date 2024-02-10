package com.cmc.zenefitserver;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableFeignClients
@EnableJpaAuditing
@EnableBatchProcessing
@EnableRedisRepositories
@SpringBootApplication
public class ZenefitServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZenefitServerApplication.class, args);
	}

}
