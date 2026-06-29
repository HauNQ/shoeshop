package com.tech.shoeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(
//		exclude = DataSourceAutoConfiguration.class
)
public class ShoeshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoeshopApplication.class, args);
	}

}
