package com.restaurante.proyecto.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.restaurante.proyecto.model.Reserva;

import reactor.core.publisher.Mono;

// Repositorio para las operaciones CRUD de las reservas del restaurante
// Se establecen métodos para buscar, eliminar y guardar reservas en la base de datos MongoDB con Spring Data MongoDB
// Se utiliza programación reactiva con Reactor para operaciones asíncronas y no bloqueantes

public interface ReservaRepository extends ReactiveMongoRepository<Reserva, String> {
    Mono<Reserva> findByCedula(String cedula);
    Mono<Void> deleteByCedula(String cedula);
}