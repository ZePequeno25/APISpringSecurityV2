package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	// Metodo principal do User Service. Inicia o Spring Boot e carrega toda a configuracao da aplicacao.
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
