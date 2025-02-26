package com.restaurante.proyecto.model;

// Clase para representar la respuesta de autenticación
// Se utiliza para devolver el token JWT generado al iniciar sesión con credenciales de usuario y contraseña en formato Basic Auth

public class AuthResponse {
    private final String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}