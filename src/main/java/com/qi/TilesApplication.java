package com.qi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(TilesApplication.class, args);
	}

}
