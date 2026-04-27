package com.tenpai.backend.tenpai_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tenpai.backend.tenpai_backend.mapper")
public class TenpaiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenpaiBackendApplication.class, args);
	}

}
