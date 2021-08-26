package com.ogefest.filehunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class LocalsearchApplication {

	private static App app = null;
	@Bean
	public App getApp() {
		if (this.app == null) {
			app = new App();
		}

		return app;
	}

	public static void main(String[] args) {
		app = new App();
//		app.start();

		System.out.println("System");
		SpringApplication.run(LocalsearchApplication.class, args);
	}

}
