package com.restaurante.proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Clase para representar un usuario en la aplicación
// Se utiliza para almacenar la información de un usuario en la base de datos MongoDB con Spring Data MongoDB
// Se establece la colección "usuarios" para almacenar los usuarios en la base de datos

@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;
    private String cedula;
    private String username;
    private String password;
    private String role; //usuarios o administradores

    public Usuario(String cedula ,String username, String password, String role) {
        this.cedula = cedula;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() { return id; }
    public String getCedula() { return cedula; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public void setId(String id) { this.id = id; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}