package com.restaurante.proyecto.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Controlador para manejar errores de acceso denegado en la aplicación
// Se establece un endpoint para devolver un mensaje de error personalizado en caso de acceso denegado

@RestController
@RequestMapping("/error")
public class ErrorController {

    private final MessageSource messageSource;

    public ErrorController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @RequestMapping("/acceso-denegado")
    public ResponseEntity<String> accesoDenegado(Locale locale) {
        String mensaje = messageSource.getMessage("error.access.denied", null, locale);
        return ResponseEntity.status(403).body(mensaje);
    }
}