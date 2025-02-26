package com.restaurante.proyecto.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.restaurante.proyecto.service.JwtUtil;

import reactor.core.publisher.Mono;

// Filtro de autenticación JWT (JSON Web Token) para la aplicación con Spring Security
// Se establece un filtro para validar el token JWT en las cabeceras de autorización de las peticiones HTTP y autenticar a los usuarios con el servicio de usuarios reactivos

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final ReactiveUserDetailsService reactiveUserDetailsService;

    @Autowired
    public JwtAuthenticationFilter(@Lazy JwtUtil jwtUtil, @Lazy ReactiveUserDetailsService reactiveUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.reactiveUserDetailsService = reactiveUserDetailsService;
    }

    // Método para filtrar las peticiones HTTP y validar el token JWT en las cabeceras de autorización
    // Se extrae el nombre de usuario del token JWT y se autentica al usuario con el servicio de usuarios reactivos

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        if (username == null) {
            return chain.filter(exchange);
        }

        return reactiveUserDetailsService.findByUsername(username)
            .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
            .flatMap(userDetails -> {
                if (!jwtUtil.validateToken(token, userDetails.getUsername())) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
            })
            .onErrorResume(e -> {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            });
    }
}
