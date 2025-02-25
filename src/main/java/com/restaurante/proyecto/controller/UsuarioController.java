package com.restaurante.proyecto.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios para realizar reservas")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final MessageSource messageSource;

    public UsuarioController(UsuarioRepository usuarioRepository, MessageSource messageSource, UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Registrar un usuario")
    @PostMapping("/registro")
    public String registrarUsuario(@RequestBody Usuario usuario, Locale locale) {
        usuarioService.registrarUsuario(usuario);
        return messageSource.getMessage("user.registration.success", null, locale);
    }

    @Operation(summary = "Obtener todos los usuarios (solo admins)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarUsuarios(Locale locale) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body(messageSource.getMessage("error.access.denied", null, locale));
        }
        return ResponseEntity.ok(usuarioRepository.findAll());
    }
    
    @Operation(summary = "Obtener un usuario por cédula solo la del usuario autenticado o un admin")
    @GetMapping("/{cedula}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtenerUsuario(@PathVariable String cedula, Locale locale) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return usuarioRepository.findByCedula(cedula).map(usuario -> {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) || usuario.getUsername().equals(username)) {
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(403).body(messageSource.getMessage("error.access.denied", null, locale));
            }
        }).orElseGet(() -> ResponseEntity.status(404)
                .body(messageSource.getMessage("user.not.found", null, locale)));
    }

    @Operation(summary = "Actualizar un usuario (solo admins)")
    @PutMapping("/{cedula}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> actualizarUsuario(@PathVariable String cedula, @RequestBody Usuario usuarioActualizado, Locale locale) {
        return usuarioRepository.findByCedula(cedula).map(usuario -> {
            usuario.setUsername(usuarioActualizado.getUsername());

            if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
            }

            usuario.setRole(usuarioActualizado.getRole());
            usuarioRepository.save(usuario);
            return ResponseEntity.ok(messageSource.getMessage("user.update.success", null, locale));
        }).orElse(ResponseEntity.status(404).body(messageSource.getMessage("user.not.found", null, locale)));
    }

    @Operation(summary = "Eliminar un usuario (solo admins)")
    @DeleteMapping("/{cedula}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarUsuario(@PathVariable String cedula, Locale locale) {
        return usuarioRepository.findByCedula(cedula).map(usuario -> {
            usuarioRepository.delete(usuario);
            return ResponseEntity.ok(messageSource.getMessage("user.delete.success", null, locale));
        }).orElse(ResponseEntity.status(404).body(messageSource.getMessage("user.not.found", null, locale)));
    }
}
