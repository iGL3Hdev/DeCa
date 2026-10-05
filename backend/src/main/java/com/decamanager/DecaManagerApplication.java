package com.decamanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.decamanager.deca.DecaProperties;

@SpringBootApplication
@EnableConfigurationProperties(DecaProperties.class)
public class DecaManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DecaManagerApplication.class, args);
	}

}
