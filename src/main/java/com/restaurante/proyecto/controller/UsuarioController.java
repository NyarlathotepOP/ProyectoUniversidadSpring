package com.restaurante.proyecto.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.proyecto.model.Usuario;
import com.restaurante.proyecto.repository.UsuarioRepository;
import com.restaurante.proyecto.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

// Controlador para las operaciones relacionadas con los usuarios para realizar reservas
// Se establecen los endpoints para registrar, obtener, actualizar y eliminar usuarios de la aplicación y sus roles
// Se establecen las anotaciones de Swagger para documentar la API de usuarios y sus operaciones

@Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios para realizar reservas")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final MessageSource messageSource;

    public UsuarioController(UsuarioRepository usuarioRepository, MessageSource messageSource, 
                             UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
    }

    // Registrar un usuario
    // Se establece un endpoint para registrar un nuevo usuario en la aplicación con un nombre de usuario, cedula, contraseña y rol de usuario o admin

    @Operation(summary = "Registrar un usuario")
    @PostMapping("/registro")
    public Mono<ResponseEntity<String>> registrarUsuario(@RequestBody Usuario usuario, Locale locale) {
        return usuarioService.registrarUsuario(usuario)
                .then(Mono.just(ResponseEntity.ok(
                        messageSource.getMessage("user.registration.success", null, locale))));
    }

    // Obtener todos los usuarios (solo admins)
    // Se establece un endpoint para obtener todos los usuarios registrados en la aplicación (solo para usuarios con rol de admin)

    @Operation(summary = "Obtener todos los usuarios (solo admins)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<?>> listarUsuarios(Locale locale) {
        return usuarioRepository.findAll()
                .collectList()
                .map(list -> ResponseEntity.ok(list));
    }

    // Obtener un usuario por cédula (solo la del usuario autenticado o un admin)
    // Se establece un endpoint para obtener un usuario registrado en la aplicación por su cédula (solo para el usuario autenticado o un admin)
    
    @Operation(summary = "Obtener un usuario por cédula (solo la del usuario autenticado o un admin)")
    @GetMapping("/{cedula}")
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<?>> obtenerUsuario(@PathVariable String cedula, Locale locale) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(auth -> 
                    usuarioRepository.findByCedula(cedula)
                        .flatMap(usuario -> {
                            // Permitir acceso si es admin o si es el propio usuario
                            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                                    || usuario.getUsername().equals(auth.getName())) {
                                return Mono.just(ResponseEntity.ok(usuario));
                            } else {
                                return Mono.just(ResponseEntity.status(403)
                                        .body(messageSource.getMessage("error.access.denied", null, locale)));
                            }
                        })
                        .switchIfEmpty(Mono.just(ResponseEntity.status(404)
                                .body(messageSource.getMessage("user.not.found", null, locale))))
                );
    }

    // Actualizar un usuario (solo admins)
    // Se establece un endpoint para actualizar un usuario registrado en la aplicación por su cédula (solo para usuarios con rol de admin)

    @Operation(summary = "Actualizar un usuario (solo admins)")
    @PutMapping("/{cedula}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<String>> actualizarUsuario(@PathVariable String cedula, @RequestBody Usuario usuarioActualizado, Locale locale) {
        return usuarioRepository.findByCedula(cedula)
                .flatMap(usuario -> {
                    usuario.setUsername(usuarioActualizado.getUsername());
                    if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
                        usuario.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
                    }
                    usuario.setRole(usuarioActualizado.getRole());
                    return usuarioRepository.save(usuario)
                            .then(Mono.just(ResponseEntity.ok(
                                    messageSource.getMessage("user.update.success", null, locale))));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(404)
                        .body(messageSource.getMessage("user.not.found", null, locale))));
    }

    // Eliminar un usuario (solo admins)
    // Se establece un endpoint para eliminar un usuario registrado en la aplicación por su cédula (solo para usuarios con rol de admin)

    @Operation(summary = "Eliminar un usuario (solo admins)")
    @DeleteMapping("/{cedula}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<String>> eliminarUsuario(@PathVariable String cedula, Locale locale) {
        return usuarioRepository.findByCedula(cedula)
                .flatMap(usuario -> usuarioRepository.delete(usuario)
                        .then(Mono.just(ResponseEntity.ok(
                                messageSource.getMessage("user.delete.success", null, locale)))))
                .switchIfEmpty(Mono.just(ResponseEntity.status(404)
                        .body(messageSource.getMessage("user.not.found", null, locale))));
    }
}