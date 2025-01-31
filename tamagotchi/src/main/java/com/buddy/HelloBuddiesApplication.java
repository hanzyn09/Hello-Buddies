package com.buddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling  // 스케줄링 활성화
@EnableAutoConfiguration(exclude = { ErrorMvcAutoConfiguration.class })
public class HelloBuddiesApplication {

	public static void main(String[] args) {
			log.info("WELCOME TO Hello Buddies!");
			SpringApplication.run(HelloBuddiesApplication.class, args);
	}

}
