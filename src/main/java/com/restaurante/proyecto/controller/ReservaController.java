package com.restaurante.proyecto.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Reservas", description = "Operaciones relacionadas con las reservas del restaurante")
@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaRepository repository;
    private final MessageSource messageSource;

    public ReservaController(ReservaRepository repository, MessageSource messageSource) {
        this.repository = repository;
        this.messageSource = messageSource;
    }

    @Operation(summary = "Obtener todas las reservas del restaurante solo admins")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getReservas(Locale locale) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(repository.findAll());
        } else {
            return ResponseEntity.status(403).body(messageSource.getMessage("error.access.denied", null, locale));
        }
    }    

    @Operation(summary = "Obtener una reserva por cédula (solo para el usuario autenticado o un admin)")
    @GetMapping("/{cedula}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtenerReserva(@PathVariable String cedula, Locale locale) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return repository.findByCedula(cedula).map(reserva -> {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) || reserva.getUsuario().equals(username)) {
                return ResponseEntity.ok(reserva);
            } else {
                return ResponseEntity.status(403).body(messageSource.getMessage("error.access.denied", null, locale));
            }
        }).orElseGet(() -> ResponseEntity.status(404)
                .body(messageSource.getMessage("reservation.not.found", null, locale)));
    }

    @Operation(summary = "Crear una reserva")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> creaReserva(@RequestBody Reserva reserva, Locale locale) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        reserva.setUsuario(username);

        repository.save(reserva);
        return ResponseEntity.ok(messageSource.getMessage("reservation.success", null, locale));
    }

    @Operation(summary = "Actualizar una reserva buscada por cedula solo para el usuario autenticado o un admin")
    @PutMapping("/{cedula}")
    public ResponseEntity<String> actualizarReserva(@PathVariable String cedula, @RequestBody Reserva nuevaReserva, Locale locale, Authentication authentication) {
        String username = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        return repository.findByCedula(cedula).map(reserva -> {
            if (esAdmin || reserva.getUsuario().equals(username)) {
                reserva.setNombreCliente(nuevaReserva.getNombreCliente());
                reserva.setFecha(nuevaReserva.getFecha());
                reserva.setNumeroPersonas(nuevaReserva.getNumeroPersonas());
                repository.save(reserva);
                return ResponseEntity.ok(messageSource.getMessage("reservation.update.success", null, locale));
            } else {
                return ResponseEntity.status(403).body(messageSource.getMessage("reservation.update.denied", null, locale));
            }
        }).orElseGet(() -> ResponseEntity.status(404).body(messageSource.getMessage("reservation.not.found", null, locale)));
    }

    @Operation(summary = "Eliminar una reserva por cédula solo para el usuario autenticado o un admin")
    @DeleteMapping("/{cedula}")
    public ResponseEntity<String> eliminarReserva(@PathVariable String cedula, Locale locale, Authentication authentication) {
        String username = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        return repository.findByCedula(cedula).map(reserva -> {
            if (esAdmin || reserva.getUsuario().equals(username)) {
                repository.delete(reserva);
                return ResponseEntity.ok(messageSource.getMessage("reservation.deleted", null, locale));
            } else {
                return ResponseEntity.status(403).body(messageSource.getMessage("reservation.delete.denied", null, locale));
            }
        }).orElseGet(() -> ResponseEntity.status(404).body(messageSource.getMessage("reservation.not.found", null, locale)));
    }
}