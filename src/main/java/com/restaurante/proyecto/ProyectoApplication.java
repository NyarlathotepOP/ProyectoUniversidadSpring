package com.restaurante.proyecto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

// Clase principal de la aplicación Spring Boot
// Se utiliza para inicializar la aplicación y ejecutarla

@SpringBootApplication
@EnableMongoRepositories
public class ProyectoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProyectoApplication.class, args);
    }
}