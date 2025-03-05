package com.restaurante.proyecto.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.restaurante.proyecto.model.Reserva;

// Pruebas de integración para el controlador de reservas de la aplicación con Spring WebFlux
// Se simula la autenticación de un usuario con el rol USER y se realizan pruebas de creación de reservas

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ReservaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCreateReserva() {
        Reserva nuevaReserva = new Reserva("usuario", "1234567890", "Cliente de Prueba", "2025-05-10", 10);

        webTestClient
            // Simulamos la autenticación de un usuario con el rol USER
            .mutateWith(SecurityMockServerConfigurers.mockUser(org.springframework.security.core.userdetails.User.withUsername("usuario").password("password").roles("USER").build()))
            // Realizamos una petición POST a la URL /reservas con la nueva reserva
            .post()
            // Establecemos el cuerpo de la petición con la nueva reserva
            .uri("/reservas")
            // Enviamos la petición al servidor
            .bodyValue(nuevaReserva)
            // Intercambiamos y validamos la respuesta del servidor con el código de estado 200 (OK)
            .exchange()
            .expectStatus().isOk()
            // Validamos el cuerpo de la respuesta con un mensaje de éxito de creación de la reserva
            .expectBody(String.class)
            .value(response -> {
                assertThat(response).contains("Reservation created successfully.");
            });
    }
}