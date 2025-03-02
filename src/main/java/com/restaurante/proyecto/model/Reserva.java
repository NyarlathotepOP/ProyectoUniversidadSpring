package com.restaurante.proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

// Clase para representar una reserva en el restaurante
// Se utiliza para almacenar la información de una reserva en la base de datos MongoDB con Spring Data MongoDB
// Se establece la colección "reservas" para almacenar las reservas en la base de datos

@Document(collection = "reservas")
public class Reserva {
    @Id
    // Se genera un identificador único para cada reserva con NanoId
    private String id = NanoIdUtils.randomNanoId(new java.util.Random(), DIGITS, 5);
    private String usuario;
    private String nombreCliente;
    private String cedula;
    private String fecha;
    private int numeroPersonas;

    // Caracteres para generar el identificador único de la reserva con NanoId
    private static final char [] DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public Reserva(String usuario, String cedula, String nombreCliente, String fecha, int numeroPersonas) {
        this.usuario = usuario;
        this.nombreCliente = nombreCliente;
        this.cedula = cedula;
        this.fecha = fecha;
        this.numeroPersonas = numeroPersonas;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public int getNumeroPersonas() { return numeroPersonas; }
    public void setNumeroPersonas(int numeroPersonas) { this.numeroPersonas = numeroPersonas; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
}