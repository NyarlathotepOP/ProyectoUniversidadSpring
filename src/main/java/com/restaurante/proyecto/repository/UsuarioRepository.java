package com.restaurante.proyecto.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.restaurante.proyecto.model.Usuario;

import reactor.core.publisher.Mono;

// Repositorio para las operaciones CRUD de los usuarios de la aplicación
// Se establecen métodos para buscar, eliminar y guardar usuarios en la base de datos MongoDB con Spring Data MongoDB
// Se utiliza programación reactiva con Reactor para operaciones asíncronas y no bloqueantes
// Se establece un método para buscar un usuario por su nombre de usuario y otro para buscar un usuario por su cédula

public interface UsuarioRepository extends ReactiveMongoRepository<Usuario, String> {
    Mono<Usuario> findByUsername(String username);
    Mono<Usuario> findByCedula(String cedula);
}