package com.cmc.zenefitserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ZenefitServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZenefitServerApplication.class, args);
	}

}
