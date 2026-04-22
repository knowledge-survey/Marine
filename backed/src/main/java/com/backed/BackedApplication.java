package com.backed;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SqlInitializationAutoConfiguration.class})
@EnableScheduling
@MapperScan("com.backed.mapper")
public class BackedApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackedApplication.class, args);
	}

}
