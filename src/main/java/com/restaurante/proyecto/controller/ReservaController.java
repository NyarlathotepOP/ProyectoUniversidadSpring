package com.restaurante.proyecto.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.proyecto.model.Reserva;
import com.restaurante.proyecto.repository.ReservaRepository;
import com.restaurante.proyecto.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

// Controlador para las operaciones relacionadas con las reservas del restaurante
// Se establecen los endpoints para obtener, crear, actualizar y eliminar reservas del restaurante
// Se establecen las anotaciones de Swagger para documentar la API de reservas del restaurante y sus operaciones

@Tag(name = "Reservas", description = "Operaciones relacionadas con las reservas del restaurante")
@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaRepository repository;
    private final MessageSource messageSource;
    private final UsuarioRepository usuarioRepository;

    public ReservaController(ReservaRepository repository, MessageSource messageSource, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.messageSource = messageSource;
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener todas las reservas del restaurante (solo admins)
    // Se establece un endpoint para obtener todas las reservas del restaurante

    @Operation(summary = "Obtener todas las reservas del restaurante (solo admins)")
    @GetMapping
    public Mono<ResponseEntity<?>> getReservas(Locale locale) {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication())
            .flatMap(auth -> {
                boolean esAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                if (esAdmin) {
                    return Mono.just(ResponseEntity.ok(repository.findAll()));
                } else {
                    String mensaje = messageSource.getMessage("error.access.denied", null, locale);
                    return Mono.just(ResponseEntity.status(403).body(mensaje));
                }
            });
    }

    // Obtener una reserva por cédula (solo para el usuario autenticado o un admin)
    // Se establece un endpoint para obtener una reserva del restaurante por cédula de cliente
    // Se verifica si el usuario autenticado es el mismo que creó la reserva o si es un admin para permitir el acceso a la reserva

    @Operation(summary = "Obtener reservas por cédula (solo para el usuario autenticado o un admin)")
    @GetMapping("/{cedula}")
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<?>> obtenerReservas(@PathVariable String cedula, Locale locale) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                var auth = ctx.getAuthentication();
                boolean esAdmin = auth.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                if (esAdmin) {
                    return repository.findByCedula(cedula)
                        .collectList()
                        .flatMap(list -> {
                            if (list.isEmpty()) {
                                String mensaje = messageSource.getMessage("reservation.not.found", null, locale);
                                return Mono.just(ResponseEntity.status(404).body(mensaje));
                            } else {
                                return Mono.just(ResponseEntity.ok(list));
                            }
                        });
                } else {
                    return usuarioRepository.findByUsername(auth.getName())
                        .flatMap(user -> {
                            if (!user.getCedula().equals(cedula)) {
                                String mensaje = messageSource.getMessage("error.access.denied", null, locale);
                                return Mono.just(ResponseEntity.status(403).body(mensaje));
                            } else {
                                return repository.findByCedula(cedula)
                                    .collectList()
                                    .flatMap(list -> {
                                        if (list.isEmpty()) {
                                            String mensaje = messageSource.getMessage("reservation.not.found", null, locale);
                                            return Mono.just(ResponseEntity.status(404).body(mensaje));
                                        } else {
                                            return Mono.just(ResponseEntity.ok(list));
                                        }
                                    });
                            }
                        });
                }
            });
    }

    // Crear una reserva
    // Se establece un endpoint para crear una nueva reserva en el restaurante con los datos de la reserva y el usuario autenticado

    @Operation(summary = "Crear una reserva")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<ResponseEntity<String>> creaReserva(@RequestBody Reserva reserva, Locale locale) {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication())
            .flatMap(auth ->
                usuarioRepository.findByUsername(auth.getName())
                    .flatMap(user -> {
                        reserva.setUsuario(auth.getName());
                        reserva.setCedula(user.getCedula());
                        return repository.save(reserva);
                    })
            )
            .then(Mono.just(ResponseEntity.ok(
                messageSource.getMessage("reservation.success", null, locale)
            )));
    }

    // Actualizar una reserva buscada por cédula (solo para el usuario autenticado o un admin)
    // Se establece un endpoint para actualizar una reserva del restaurante buscada por cédula de cliente

    @Operation(summary = "Actualizar una reserva buscada por id (solo para el usuario autenticado o un admin)")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<String>> actualizarReserva(@PathVariable String id, @RequestBody Reserva nuevaReserva, Locale locale) {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication())
            .flatMap(auth -> {
                String username = auth.getName();
                boolean esAdmin = auth.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> 
                            grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
                return repository.findById(id).flatMap(reserva -> {
                    if (esAdmin || reserva.getUsuario().equals(username)) {
                        reserva.setNombreCliente(nuevaReserva.getNombreCliente());
                        reserva.setFecha(nuevaReserva.getFecha());
                        reserva.setNumeroPersonas(nuevaReserva.getNumeroPersonas());
                        return repository.save(reserva)
                            .then(Mono.just(ResponseEntity.ok(
                                messageSource.getMessage("reservation.update.success", null, locale))));
                    } else {
                        return Mono.just(ResponseEntity.status(403)
                            .body(messageSource.getMessage("reservation.update.denied", null, locale)));
                    }
                }).switchIfEmpty(Mono.defer(() ->
                        Mono.just(ResponseEntity.status(404)
                            .body(messageSource.getMessage("reservation.not.found", null, locale)))));
            });
    }    

    // Eliminar una reserva por cédula (solo para el usuario autenticado o un admin)
    // Se establece un endpoint para eliminar una reserva del restaurante por cédula de cliente

    @Operation(summary = "Eliminar una reserva por id (solo para el usuario autenticado o un admin)")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<String>> eliminarReserva(@PathVariable String id, Locale locale) {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication())
            .flatMap(auth -> {
                String username = auth.getName();
                boolean esAdmin = auth.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
                return repository.findById(id).flatMap(reserva -> {
                    if (esAdmin || reserva.getUsuario().equals(username)) {
                        return repository.delete(reserva)
                                .then(Mono.just(ResponseEntity.ok(
                                        messageSource.getMessage("reservation.deleted", null, locale))));
                    } else {
                        return Mono.just(ResponseEntity.status(403)
                                .body(messageSource.getMessage("reservation.delete.denied", null, locale)));
                    }
                }).switchIfEmpty(Mono.defer(() ->
                        Mono.just(ResponseEntity.status(404)
                                .body(messageSource.getMessage("reservation.not.found", null, locale)))));
            });
    }
}