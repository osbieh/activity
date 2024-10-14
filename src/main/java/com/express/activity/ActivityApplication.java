package com.express.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.express.activity.repository")
public class ActivityApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivityApplication.class, args);
	}

}
