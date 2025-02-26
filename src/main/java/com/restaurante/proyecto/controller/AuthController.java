package com.restaurante.proyecto.controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.proyecto.model.AuthResponse;
import com.restaurante.proyecto.service.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;

// Controlador para la autenticación de usuarios y generación de tokens JWT
// Se establece un endpoint para iniciar sesión con credenciales de usuario y contraseña en formato Basic Auth y generar el token de acceso JWT

@RestController
@RequestMapping("/usuarios")
public class AuthController {

    @Autowired
    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Iniciar sesión con credenciales de usuario y contraseña en formato Basic Auth (solo para usuarios registrados) genera el token de acceso JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String authHeader) {
        String[] credentials = new String(Base64.getDecoder().decode(authHeader.split(" ")[1])).split(":");
        String username = credentials[0];
        String password = credentials[1];

        reactiveAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        String token = jwtUtil.generateToken(username);
        
        return ResponseEntity.ok(new AuthResponse(token));
    }
}