package com.dis.Idp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import utils.Log;

@SpringBootApplication
public class IdpApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdpApplication.class, args);
		Log.info("Just for fun to check workinig");
	}

}
