# 💰 EasySave Service - API REST

Servicio de gestión de usuarios, ingresos y gastos desarrollado con Spring Boot.

## 📋 Entidades

### Usuario
- `id`: Identificador único
- `rol`: Rol del usuario (ADMIN, USER)
- `correo`: Email único
- `username`: Nombre de usuario único
- `password`: Contraseña
- `ingresos`: Lista de ingresos del usuario
- `gastos`: Lista de gastos del usuario

### Ingreso
- `id`: Identificador único
- `nombreIngreso`: Nombre del ingreso
- `valorIngreso`: Valor monetario
- `estadoIngreso`: Tipo ("fijo" o "variable")
- `usuario`: Usuario propietario

### Gasto
- `id`: Identificador único
- `nombreGasto`: Nombre del gasto
- `valorGasto`: Valor monetario
- `estadoGasto`: Tipo ("fijo" o "variable")
- `usuario`: Usuario propietario

## 🚀 Endpoints de la API

### 👥 Usuarios

#### Listar todos los usuarios (con sus ingresos y gastos)
```http
GET http://localhost:8080/api/v1/usuario-service/usuarios
```

#### Obtener un usuario por ID (con sus ingresos y gastos)
```http
GET http://localhost:8080/api/v1/usuario-service/usuarios/{id}
```

#### Crear un nuevo usuario
```http
POST http://localhost:8080/api/v1/usuario-service/usuario
Content-Type: application/json

{
  "rol": "USER",
  "correo": "nuevo@gmail.com",
  "username": "nuevouser",
  "password": "password123"
}
```

#### Actualizar un usuario
```http
PUT http://localhost:8080/api/v1/usuario-service/usuario
Content-Type: application/json

{
  "id": 1,
  "rol": "ADMIN",
  "correo": "actualizado@gmail.com",
  "username": "useractualizado",
  "password": "newpass456"
}
```

#### Eliminar un usuario
```http
DELETE http://localhost:8080/api/v1/usuario-service/usuarios/{id}
```

---

### 💵 Ingresos

#### Listar todos los ingresos
```http
GET http://localhost:8080/api/v1/usuario-service/ingresos
```

#### Obtener un ingreso por ID
```http
GET http://localhost:8080/api/v1/usuario-service/ingresos/{id}
```

#### Listar ingresos de un usuario específico
```http
GET http://localhost:8080/api/v1/usuario-service/usuarios/{usuarioId}/ingresos
```

#### Crear un nuevo ingreso para un usuario
```http
POST http://localhost:8080/api/v1/usuario-service/usuarios/{usuarioId}/ingresos
Content-Type: application/json

{
  "nombreIngreso": "Salario",
  "valorIngreso": 3000000,
  "estadoIngreso": "fijo"
}
```

#### Actualizar un ingreso
```http
PUT http://localhost:8080/api/v1/usuario-service/ingresos/{id}
Content-Type: application/json

{
  "nombreIngreso": "Salario Actualizado",
  "valorIngreso": 3500000,
  "estadoIngreso": "fijo"
}
```

#### Eliminar un ingreso
```http
DELETE http://localhost:8080/api/v1/usuario-service/ingresos/{id}
```

#### Obtener ingresos por estado (fijo o variable)
```http
GET http://localhost:8080/api/v1/usuario-service/ingresos/estado/{estado}
```
*Ejemplos: `/ingresos/estado/fijo` o `/ingresos/estado/variable`*

---

### 💸 Gastos

#### Listar todos los gastos
```http
GET http://localhost:8080/api/v1/usuario-service/gastos
```

#### Obtener un gasto por ID
```http
GET http://localhost:8080/api/v1/usuario-service/gastos/{id}
```

#### Listar gastos de un usuario específico
```http
GET http://localhost:8080/api/v1/usuario-service/usuarios/{usuarioId}/gastos
```

#### Crear un nuevo gasto para un usuario
```http
POST http://localhost:8080/api/v1/usuario-service/usuarios/{usuarioId}/gastos
Content-Type: application/json

{
  "nombreGasto": "Arriendo",
  "valorGasto": 800000,
  "estadoGasto": "fijo"
}
```

#### Actualizar un gasto
```http
PUT http://localhost:8080/api/v1/usuario-service/gastos/{id}
Content-Type: application/json

{
  "nombreGasto": "Arriendo Actualizado",
  "valorGasto": 850000,
  "estadoGasto": "fijo"
}
```

#### Eliminar un gasto
```http
DELETE http://localhost:8080/api/v1/usuario-service/gastos/{id}
```

#### Obtener gastos por estado (fijo o variable)
```http
GET http://localhost:8080/api/v1/usuario-service/gastos/estado/{estado}
```
*Ejemplos: `/gastos/estado/fijo` o `/gastos/estado/variable`*

---

## ⚙️ Configuración

### Base de Datos
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/easyusuarios
spring.datasource.username=postgres
spring.datasource.password=a1b2c3d4
```

### Puerto del Servidor
```
http://localhost:8080
```

## 📦 Tecnologías Utilizadas

- **Spring Boot 3.3.3**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **Maven**

## 🎯 Características Principales

✅ **Relaciones OneToMany**: Un usuario puede tener múltiples ingresos y gastos
✅ **Cascade**: Al eliminar un usuario, se eliminan automáticamente sus ingresos y gastos
✅ **OrphanRemoval**: Los ingresos/gastos sin usuario se eliminan automáticamente
✅ **Gestión de estados**: Clasificación de ingresos y gastos como "fijo" o "variable"
✅ **Datos de ejemplo**: Se cargan automáticamente al iniciar la aplicación

## 🔄 Ejemplo de Flujo Completo

1. **Obtener un usuario con sus ingresos y gastos:**
   ```
   GET http://localhost:8080/api/v1/usuario-service/usuarios/2
   ```

2. **Agregar un nuevo ingreso al usuario:**
   ```
   POST http://localhost:8080/api/v1/usuario-service/usuarios/2/ingresos
   {
     "nombreIngreso": "Proyecto Extra",
     "valorIngreso": 1000000,
     "estadoIngreso": "variable"
   }
   ```

3. **Agregar un nuevo gasto al usuario:**
   ```
   POST http://localhost:8080/api/v1/usuario-service/usuarios/2/gastos
   {
     "nombreGasto": "Gimnasio", 
     "valorGasto": 100000,
     "estadoGasto": "fijo"
   }
   ```

4. **Ver los ingresos actualizados del usuario:**
   ```
   GET http://localhost:8080/api/v1/usuario-service/usuarios/2/ingresos
   ```

5. **Eliminar un gasto:**
   ```
   DELETE http://localhost:8080/api/v1/usuario-service/gastos/1
   ```

## 🚀 Ejecutar el Proyecto

1. Asegúrate de tener PostgreSQL ejecutándose
2. Crea la base de datos: `CREATE DATABASE easyusuarios;`
3. Ejecuta la aplicación: `mvn spring-boot:run`
4. La aplicación estará disponible en: `http://localhost:8080`

---

**Nota**: Al consultar un usuario, automáticamente se incluyen sus listas de ingresos y gastos en la respuesta JSON. 🎉
