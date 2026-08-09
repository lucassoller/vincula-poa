package com.vincula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VinculaPoaApplication {

	public static void main(String[] args) {
		SpringApplication.run(VinculaPoaApplication.class, args);
	}
}