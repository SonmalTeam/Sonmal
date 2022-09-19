package com.d202.assemble;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AssembleServerApplication {

	public static void main(String[] args) {
		System.out.println("build test01");
		SpringApplication.run(AssembleServerApplication.class, args);
	}

}
