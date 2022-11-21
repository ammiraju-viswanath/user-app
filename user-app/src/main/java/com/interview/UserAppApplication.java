package com.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class UserAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserAppApplication.class, args);
	}

	@Bean
	@LoadBalanced
	RestTemplate loadRestTempolate() {
		return new RestTemplate();
	}
}
