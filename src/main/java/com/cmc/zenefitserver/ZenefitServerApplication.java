package com.cmc.zenefitserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ZenefitServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZenefitServerApplication.class, args);
	}

}
