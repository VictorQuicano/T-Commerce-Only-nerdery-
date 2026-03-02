package com.tcommerce.TCommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TCommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TCommerceApplication.class, args);
	}

}
