package com.restaurante.proyecto.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

// Pruebas de seguridad para el controlador de reservas de la aplicación con Spring WebFlux
// Se simulan las solicitudes HTTP a las rutas protegidas del controlador de reservas y se validan los errores de acceso

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ReservaControllerSecurityTest {

    @Autowired
    private WebTestClient webTestClient;

    // Prueba de acceso a la ruta protegida /reservas sin un token JWT
    // Se espera un error 401 Unauthorized al intentar acceder a la ruta protegida sin un token

    @Test
    void testRequestWithoutToken() {
        webTestClient.get()
            .uri("/reservas")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    // Prueba de acceso a la ruta protegida /reservas con un rol incorrecto en el token JWT
    // Se espera un error 403 Forbidden al intentar acceder a la ruta protegida con un token JWT que no contiene el rol correcto

    @Test
    void testAccessWithInvalidJwt() {
        webTestClient
            .mutateWith(SecurityMockServerConfigurers.mockJwt()
                .jwt(jwt -> jwt.claim("sub", "usuario"))
                .authorities(new SimpleGrantedAuthority("TEST"))
            )
            .get()
            .uri("/reservas")
            .exchange()
            .expectStatus().isForbidden();
    }
}