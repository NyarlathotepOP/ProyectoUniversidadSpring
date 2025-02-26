package com.restaurante.proyecto.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

import com.restaurante.proyecto.filters.JwtAuthenticationFilter;

import reactor.core.publisher.Mono;

// Configuración de seguridad para la aplicación con Spring Security y JWT (JSON Web Token)
// Se establecen las reglas de seguridad para los endpoints de la aplicación y se configura el filtro de autenticación JWT

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Configuración de un codificador de contraseñas BCrypt para almacenar las contraseñas de los usuarios en la base de datos

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Manejador de acceso denegado para redirigir a la página de error de acceso denegado (403 Forbidden)

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, ex) -> {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            exchange.getResponse().getHeaders().setLocation(URI.create("/error/acceso-denegado"));
            return Mono.empty();
        };
    }

    // Configuración del administrador de autenticación reactiva para autenticar a los usuarios con el servicio de usuarios reactivos y el codificador de contraseñas

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService reactiveUserDetailsService , PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
        manager.setPasswordEncoder(passwordEncoder);
        return manager;
    }

    // Configuración de las reglas de seguridad para los endpoints de la aplicación y el filtro de autenticación JWT para autenticar a los usuarios

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager reactiveAuthenticationManager) throws Exception {
        http
            .authorizeExchange(auth -> auth

                // Se permiten las rutas de Swagger y los endpoints de registro y inicio de sesión de usuarios sin autenticación

                .pathMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/usuarios/registro",
                    "/usuarios/login"
                ).permitAll()

                // Se establecen las reglas de seguridad para los endpoints de la aplicación según el rol del usuario autenticado

                .pathMatchers(
                    "/usuarios",
                    "/reservas"
                    ).hasAnyRole("ADMIN", "USER")

                .pathMatchers(
                    "/usuarios/{cedula}",
                    "/reservas/{cedula}",
                    "/reservas/**"
                ).authenticated()

                .anyExchange().authenticated()

            )
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler()))
            .authenticationManager(reactiveAuthenticationManager)
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}