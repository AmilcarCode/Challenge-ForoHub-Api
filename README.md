# Foro Hub Challenge Back End

🚀 Proyecto de desarrollo backend correspondiente al Challenge de Alura Latam. Se trata de una API REST diseñada para administrar un foro con temática educativa, en el cual los usuarios registrados tienen la posibilidad de crear nuevos tópicos de discusión, consultar el listado de todos los existentes, ver el detalle de un tópico específico, actualizar su contenido o eliminarlo.

Además, el sistema permite la interacción entre los participantes mediante la publicación de respuestas dentro de cada tópico. La seguridad está implementada a través de un mecanismo de autenticación basado en tokens JWT (JSON Web Token), y la gestión de permisos se realiza mediante un esquema de roles que define qué acciones puede realizar cada tipo de usuario sobre los recursos disponibles.

## 🧰 Tecnologías utilizadas

- Java 21
- Spring Boot
- PostgreSQL
- Spring Security + JWT
- JPA + Hibernate
- Maven

## 📌 Funcionalidades

- Crear un nuevo tópico `POST /topics`
- Listar todos los tópicos `GET /topics`
- Ver un tópico específico `GET /topics/{id}`
- Editar un tópico `PUT /topics/{id}`
- Eliminar un tópico `DELETE /topics/{id}`
- Autenticación de usuarios con JWT
- Roles para usuarios, moderadores y administradores

## 📊 Ejemplos de uso

### Crear un nuevo tópico
```json
POST http://localhost:8080/topics

{
  "title": "Un título de ejemplo",
  "message": "Contenido del mensaje",
  "authorName": "User Test",
  "courseName": "Spring Boot"
}
```

### Actualizar un tópico existente
```json
PUT http://localhost:8080/topics/{id}

{
  "title": "Título actualizado",
  "message": "Mensaje actualizado",
  "courseName": "Curso Test"
}
```

### Listar tópicos con filtros
```
GET http://localhost:8080/topics?courseName=Curso%20Test&year=2022
```

## 🧪 Validaciones y reglas de negocio

- Verificación de campos obligatorios
- Asociación del tópico y respuesta al autor autenticado
- Control de acceso según roles
- Validación de existencia de curso y autor

## 🔐 Seguridad

- Autenticación basada en JWT (JSON Web Token): el sistema utiliza tokens firmados digitalmente para verificar la identidad de los usuarios en cada solicitud.

- Gestión de roles: se definen tres niveles de acceso — USER, MODERATOR y ADMIN — cada uno con permisos específicos para interactuar con la API.

- Protección de rutas: la seguridad de los endpoints se garantiza mediante filtros y anotaciones, restringiendo el acceso únicamente a usuarios autorizados según su rol y privilegios.
 
### Autenticación
```json
POST http://localhost:8080/login

{
  "login": "test@gmail.com",
  "password": "123456"
}
```

## 🎯 Próximas mejoras

- Documentación con Swagger
- Paginación y ordenamiento
- Pruebas unitarias con JUnit y Mockito

## 📂 Estructura del proyecto

```plaintext
src/
├── main/
│   ├── java/com/foro/hub/foro_hub_api/
│   │   ├── controller/
│   │   ├── domain/
│   │   ├── repository/
│   │   ├── service/
│   │   └── config/
│   └── resources/
│       ├── db/migration/
│       └── application.properties
├── test
    ├── java/com/foro/hub/foro_hub_api/
        ├──ForoHubTests
```

## 📋 Requisitos previos

- JDK 21
- PostgreSQL
- Maven

## ⚙️ Configuración y ejecución

1. Clona el repositorio
2. Configura las propiedades de la base de datos en `src\main\resources\application.properties`
3. Ejecuta `./mvnw spring-boot:run` (Linux/Mac) o `mvnw.cmd spring-boot:run` (Windows)
