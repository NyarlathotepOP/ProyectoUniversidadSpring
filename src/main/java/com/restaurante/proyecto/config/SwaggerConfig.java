package com.restaurante.proyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

// Configuración de Swagger para la documentación de la API de reservas del restaurante
// Se establece el título, la versión y la descripción de la API para mostrar en la documentación

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Reservas - Restaurante Proyecto")
                        .version("1.0")
                        .description("Documentación de la API de reservas para el restaurante"));
    }
}