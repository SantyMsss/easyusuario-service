# Usuario Service API

## Descripción
Servicio REST API para la gestión de usuarios con arquitectura de capas (Controller, Service, DAO, Entity).

## Tecnologías
- Java
- Spring Boot
- JPA/Hibernate
- PostgreSQL
- Lombok

## Modelo de Datos - Entidad Usuario

La entidad `Usuario` tiene los siguientes atributos:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Identificador único (auto-incremental) |
| username | String | Nombre de usuario |
| correoElectronico | String | Correo electrónico del usuario |
| password | String | Contraseña del usuario |
| celular | String | Número de celular |
| ingresos | Double | Ingresos mensuales |
| gastos | Double | Gastos mensuales |

## Arquitectura por Capas

### 1. Capa de Entidad (Entity)
- **Clase**: `Usuario`
- **Ubicación**: `co.edu.uceva.celularservice.model.entities`
- **Responsabilidad**: Representa la tabla de la base de datos

### 2. Capa de Acceso a Datos (DAO)
- **Interface**: `UsuarioDao`
- **Ubicación**: `co.edu.uceva.celularservice.model.dao`
- **Responsabilidad**: Operaciones CRUD heredadas de `CrudRepository`

### 3. Capa de Servicio (Service)
- **Interface**: `IUsuarioService`
- **Implementación**: `UsuarioServiceImpl`
- **Ubicación**: `co.edu.uceva.celularservice.model.service`
- **Responsabilidad**: Lógica de negocio

### 4. Capa de Controlador (Controller)
- **Clase**: `UsuarioRestController`
- **Ubicación**: `co.edu.uceva.celularservice.controller`
- **Responsabilidad**: Endpoints REST API

## Endpoints Disponibles

### Base URL
```
http://localhost:8080/api/v1/usuario-service
```

### 1. Listar todos los usuarios
- **Método**: `GET`
- **URL**: `/usuarios`
- **Descripción**: Retorna la lista de todos los usuarios
- **Respuesta**: Array de objetos Usuario

**Ejemplo:**
```bash
GET http://localhost:8080/api/v1/usuario-service/usuarios
```

### 2. Buscar usuario por ID
- **Método**: `GET`
- **URL**: `/usuarios/{id}`
- **Descripción**: Retorna un usuario específico por su ID
- **Parámetros**: 
  - `id` (Long): ID del usuario
- **Respuesta**: Objeto Usuario

**Ejemplo:**
```bash
GET http://localhost:8080/api/v1/usuario-service/usuarios/1
```

### 3. Crear nuevo usuario
- **Método**: `POST`
- **URL**: `/usuario`
- **Descripción**: Crea un nuevo usuario
- **Body**: Objeto Usuario (sin ID)
- **Respuesta**: Usuario creado con su ID asignado

**Ejemplo:**
```bash
POST http://localhost:8080/api/v1/usuario-service/usuario
Content-Type: application/json

{
  "username": "nuevousuario",
  "correoElectronico": "nuevo@email.com",
  "password": "mipassword",
  "celular": "3001234567",
  "ingresos": 3000000.0,
  "gastos": 1500000.0
}
```

### 4. Actualizar usuario
- **Método**: `PUT`
- **URL**: `/usuario`
- **Descripción**: Actualiza un usuario existente
- **Body**: Objeto Usuario (con ID)
- **Respuesta**: Usuario actualizado

**Ejemplo:**
```bash
PUT http://localhost:8080/api/v1/usuario-service/usuario
Content-Type: application/json

{
  "id": 1,
  "username": "usuarioactualizado",
  "correoElectronico": "actualizado@email.com",
  "password": "nuevopassword",
  "celular": "3109876543",
  "ingresos": 3500000.0,
  "gastos": 1800000.0
}
```

### 5. Eliminar usuario
- **Método**: `DELETE`
- **URL**: `/usuarios/{id}`
- **Descripción**: Elimina un usuario por su ID
- **Parámetros**: 
  - `id` (Long): ID del usuario a eliminar
- **Respuesta**: Sin contenido

**Ejemplo:**
```bash
DELETE http://localhost:8080/api/v1/usuario-service/usuarios/1
```

## Configuración de Base de Datos

El archivo `application.properties` contiene la configuración de PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/curso_sprintboot
spring.datasource.username=devdb
spring.datasource.password=a1b2c3d4
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

## Datos de Prueba

El archivo `import.sql` contiene 10 usuarios de ejemplo que se cargan automáticamente al iniciar la aplicación.

## Ejecución

```bash
./mvnw spring-boot:run
```

o en Windows:
```bash
mvnw.cmd spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`
