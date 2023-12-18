package com.soeun.GiftFunding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GiftFundingApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiftFundingApplication.class, args);
	}

}
