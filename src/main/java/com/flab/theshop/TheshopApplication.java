package com.flab.theshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class TheshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheshopApplication.class, args);
	}

}
