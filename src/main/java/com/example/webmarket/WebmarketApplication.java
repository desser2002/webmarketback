package com.example.webmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class WebmarketApplication {

	private static final Logger logger = LoggerFactory.getLogger(WebmarketApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WebmarketApplication.class, args);
	}
}
