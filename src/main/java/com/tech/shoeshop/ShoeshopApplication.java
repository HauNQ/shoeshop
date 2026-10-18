package com.tech.shoeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
//		exclude = DataSourceAutoConfiguration.class
)
@EnableRetry
@EnableScheduling
public class ShoeshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoeshopApplication.class, args);
	}

}
