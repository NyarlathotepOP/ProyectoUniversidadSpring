# Sistema de Gestión de Reservas para Restaurante

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-blue)](https://www.mongodb.com)
[![JWT](https://img.shields.io/badge/JWT-Auth-orange)](https://jwt.io)

Proyecto universitario final.

Aplicación backend para gestionar reservas en un restaurante, con autenticación JWT y arquitectura reactiva.

## Tecnologías Utilizadas
- **Spring Boot 3**: Marco principal del proyecto.
- **Spring WebFlux**: Manejo reactivo de peticiones HTTP.
- **MongoDB**: Base de datos NoSQL para almacenamiento.
- **JWT (JSON Web Tokens)**: Autenticación segura.
- **Swagger/OpenAPI 3**: Documentación interactiva de la API.
- **Project Reactor**: Programación reactiva con `Mono` y `Flux`.

## Prerrequisitos
- **Java 17+**: JDK instalado.
- **MongoDB**: Instalado y en ejecución (`mongod`).
- **Maven/Gradle**: Gestor de dependencias (se usa Maven en este proyecto).

## Instalación y Ejecución

### 1. Clonar el Repositorio
git clone https://github.com/NyarlathotepOP/ProyectoUniversidadSpring.git

cd proyecto-restaurante

### 2. Configurar MongoDB
Asegúrate de que MongoDB esté ejecutándose localmente en el puerto 27017 (configuración por defecto).

### 3. Configurar Variables de Entorno
Edita el archivo src/main/resources/application.properties:

spring.data.mongodb.uri=mongodb://localhost:27017/restaurante

jwt.secret=**********

### 4. Compilar y Ejecutar
mvn spring-boot:run

La aplicación estará disponible en: http://localhost:8080.



## Características Adicionales
- **Internacionalización**: Soporta mensajes en múltiples idiomas (inglés/español/frances) mediante la cabecera `Accept-Language`.
- **Gestión de Roles**: 
  - `ADMIN`: Acceso total a usuarios y reservas.
  - `USER`: Solo gestiona sus propias reservas.


 
## Documentación de la API
Accede a la interfaz de Swagger para explorar los endpoints:

Swagger UI

http://localhost:8080/swagger-ui.html

 - Ejemplo de endpoints:

    - Registro de usuario: POST /usuarios/registro

    - Login: POST /usuarios/login

    - Crear reserva: POST /reservas


