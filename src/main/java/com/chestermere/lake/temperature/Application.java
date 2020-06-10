package com.chestermere.lake.temperature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class Application {

	private static Server server;

	public static void main(String[] args) {
		server = new Server();
		SpringApplication.run(Application.class, args);
	}

	public static Server getServer() {
		return server;
	}

}
