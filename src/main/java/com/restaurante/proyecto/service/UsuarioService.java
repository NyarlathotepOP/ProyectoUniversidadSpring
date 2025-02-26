package com.restaurante.proyecto.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.restaurante.proyecto.model.Usuario;
import com.restaurante.proyecto.repository.UsuarioRepository;

import reactor.core.publisher.Mono;

// Servicio para autenticar a los usuarios con Spring Security y JWT (JSON Web Token)
// Se implementa la interfaz ReactiveUserDetailsService para buscar usuarios por su nombre de usuario y autenticarlos con Spring Security

@Service
public class UsuarioService implements ReactiveUserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Usuario no encontrado")))
                .map(usuario -> User.builder()
                        .username(usuario.getUsername())
                        .password(usuario.getPassword())
                        .authorities(usuario.getRole())
                        .build());
    }

    public Mono<Usuario> registrarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRole("ROLE_" + usuario.getRole().toUpperCase());
        return usuarioRepository.save(usuario);
    }
}