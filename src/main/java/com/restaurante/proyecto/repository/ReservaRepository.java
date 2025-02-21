package com.restaurante.proyecto.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.restaurante.proyecto.model.Reserva;

public interface ReservaRepository extends MongoRepository<Reserva, String> {
    Optional<Reserva> findByCedula(String cedula);
    void deleteByCedula(String cedula);
}
