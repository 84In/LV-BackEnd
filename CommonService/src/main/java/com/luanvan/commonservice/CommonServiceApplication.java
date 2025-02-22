package com.luanvan.commonservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class CommonServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommonServiceApplication.class, args);
	}

}
