package com.ogefest.filehunter;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalsearchApplication {

	public static void main(String[] args) {
		App app = new App();
		app.start();

//		System.out.println("System");
//		SpringApplication.run(LocalsearchApplication.class, args);
	}

}
