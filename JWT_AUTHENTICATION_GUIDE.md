# 🔐 Guía de Autenticación JWT - EasySave Service

## 📋 Descripción

Autenticación basada en JWT (JSON Web Token) implementada con Spring Security para ser consumida desde Flutter.

## 🔑 Endpoints de Autenticación

### Base URL
```
http://localhost:8080/api/v1/auth
```

---

## 1️⃣ Registro de Usuario

### **POST** `/api/v1/auth/register`

Registra un nuevo usuario y retorna un token JWT.

**Request Body:**
```json
{
  "username": "juan",
  "correo": "juan@example.com",
  "password": "password123",
  "rol": "USER"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "juan",
  "correo": "juan@example.com",
  "rol": "USER"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Error en el registro",
  "message": "El username ya está en uso"
}
```

---

## 2️⃣ Login de Usuario

### **POST** `/api/v1/auth/login`

Autentica un usuario existente y retorna un token JWT.

**Request Body:**
```json
{
  "username": "juan",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "juan",
  "correo": "juan@example.com",
  "rol": "USER"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "error": "Credenciales inválidas",
  "message": "Bad credentials"
}
```

---

## 3️⃣ Test de Autenticación

### **GET** `/api/v1/auth/test`

Verifica que el token JWT sea válido. **Requiere autenticación.**

**Headers:**
```
Authorization: Bearer {tu-token-jwt}
```

**Response (200 OK):**
```json
{
  "message": "¡Autenticación exitosa!",
  "status": "authenticated"
}
```

---

## 🔒 Endpoints Protegidos

Todos los endpoints de la API (excepto `/api/v1/auth/**`) requieren autenticación JWT.

**Headers necesarios:**
```
Authorization: Bearer {tu-token-jwt}
```

### Ejemplo de endpoints protegidos:
- `GET /api/v1/usuario-service/usuarios`
- `GET /api/v1/usuario-service/usuarios/{id}`
- `POST /api/v1/usuario-service/usuarios/{usuarioId}/ingresos`
- `GET /api/v1/usuario-service/usuarios/{id}/resumen-financiero`

---

## 📱 Implementación en Flutter

### 1. Instalación de dependencias

Agrega a tu `pubspec.yaml`:

```yaml
dependencies:
  http: ^1.1.0
  shared_preferences: ^2.2.2
  flutter_secure_storage: ^9.0.0
```

### 2. Servicio de Autenticación

```dart
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AuthService {
  static const String baseUrl = 'http://10.0.2.2:8080/api/v1/auth'; // Para emulador Android
  // Para dispositivo físico: 'http://TU_IP:8080/api/v1/auth'
  
  final storage = const FlutterSecureStorage();

  // Registro
  Future<Map<String, dynamic>> register({
    required String username,
    required String correo,
    required String password,
    String rol = 'USER',
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/register'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'username': username,
          'correo': correo,
          'password': password,
          'rol': rol,
        }),
      );

      if (response.statusCode == 201) {
        final data = jsonDecode(response.body);
        await saveToken(data['token']);
        return {'success': true, 'data': data};
      } else {
        final error = jsonDecode(response.body);
        return {'success': false, 'message': error['message']};
      }
    } catch (e) {
      return {'success': false, 'message': 'Error de conexión: $e'};
    }
  }

  // Login
  Future<Map<String, dynamic>> login({
    required String username,
    required String password,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/login'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'username': username,
          'password': password,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        await saveToken(data['token']);
        return {'success': true, 'data': data};
      } else {
        final error = jsonDecode(response.body);
        return {'success': false, 'message': error['message']};
      }
    } catch (e) {
      return {'success': false, 'message': 'Error de conexión: $e'};
    }
  }

  // Guardar token
  Future<void> saveToken(String token) async {
    await storage.write(key: 'jwt_token', value: token);
  }

  // Obtener token
  Future<String?> getToken() async {
    return await storage.read(key: 'jwt_token');
  }

  // Eliminar token (logout)
  Future<void> logout() async {
    await storage.delete(key: 'jwt_token');
  }

  // Verificar si está autenticado
  Future<bool> isAuthenticated() async {
    String? token = await getToken();
    return token != null;
  }

  // Hacer petición autenticada
  Future<http.Response> authenticatedRequest({
    required String url,
    String method = 'GET',
    Map<String, dynamic>? body,
  }) async {
    String? token = await getToken();
    
    Map<String, String> headers = {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token',
    };

    switch (method) {
      case 'POST':
        return await http.post(
          Uri.parse(url),
          headers: headers,
          body: body != null ? jsonEncode(body) : null,
        );
      case 'PUT':
        return await http.put(
          Uri.parse(url),
          headers: headers,
          body: body != null ? jsonEncode(body) : null,
        );
      case 'DELETE':
        return await http.delete(Uri.parse(url), headers: headers);
      default:
        return await http.get(Uri.parse(url), headers: headers);
    }
  }
}
```

### 3. Ejemplo de Pantalla de Login

```dart
import 'package:flutter/material.dart';

class LoginScreen extends StatefulWidget {
  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _usernameController = TextEditingController();
  final _passwordController = TextEditingController();
  final _authService = AuthService();
  bool _isLoading = false;

  Future<void> _login() async {
    if (_formKey.currentState!.validate()) {
      setState(() => _isLoading = true);

      final result = await _authService.login(
        username: _usernameController.text,
        password: _passwordController.text,
      );

      setState(() => _isLoading = false);

      if (result['success']) {
        Navigator.pushReplacementNamed(context, '/home');
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(result['message'])),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Login')),
      body: Padding(
        padding: EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              TextFormField(
                controller: _usernameController,
                decoration: InputDecoration(labelText: 'Username'),
                validator: (value) => value!.isEmpty ? 'Requerido' : null,
              ),
              SizedBox(height: 16),
              TextFormField(
                controller: _passwordController,
                decoration: InputDecoration(labelText: 'Password'),
                obscureText: true,
                validator: (value) => value!.isEmpty ? 'Requerido' : null,
              ),
              SizedBox(height: 24),
              _isLoading
                  ? CircularProgressIndicator()
                  : ElevatedButton(
                      onPressed: _login,
                      child: Text('Iniciar Sesión'),
                    ),
            ],
          ),
        ),
      ),
    );
  }
}
```

### 4. Ejemplo de Uso de API Protegida

```dart
// Obtener resumen financiero del usuario
Future<void> getResumenFinanciero(int usuarioId) async {
  final authService = AuthService();
  
  final response = await authService.authenticatedRequest(
    url: 'http://10.0.2.2:8080/api/v1/usuario-service/usuarios/$usuarioId/resumen-financiero',
    method: 'GET',
  );

  if (response.statusCode == 200) {
    final data = jsonDecode(response.body);
    print('Total Ingresos: ${data['totalIngresos']}');
    print('Total Gastos: ${data['totalGastos']}');
    print('Balance: ${data['balance']}');
  } else {
    print('Error: ${response.statusCode}');
  }
}
```

---

## ⚙️ Configuración

### Tiempo de expiración del token
Por defecto: **24 horas (86400000 ms)**

Para cambiar, edita `application.properties`:
```properties
app.jwt.expiration=3600000  # 1 hora
```

### Secret Key
Cambia el secret en producción en `application.properties`:
```properties
app.jwt.secret=TuClaveSecretaSuperSeguraYLarga
```

---

## 🔐 Roles de Usuario

- **USER**: Usuario normal
- **ADMIN**: Administrador (para futuras funcionalidades)

---

## ✅ Flujo de Autenticación

1. **Usuario se registra** → Recibe token JWT
2. **Usuario hace login** → Recibe token JWT
3. **Token se guarda** en `flutter_secure_storage`
4. **Cada petición** incluye el token en el header `Authorization: Bearer {token}`
5. **Backend valida** el token en cada petición
6. **Si token es válido** → Permite acceso
7. **Si token es inválido/expirado** → Retorna 401 Unauthorized

---

## 🧪 Prueba con Postman

### 1. Registro
```
POST http://localhost:8080/api/v1/auth/register
Body (JSON):
{
  "username": "test",
  "correo": "test@example.com",
  "password": "test123",
  "rol": "USER"
}
```

### 2. Login
```
POST http://localhost:8080/api/v1/auth/login
Body (JSON):
{
  "username": "test",
  "password": "test123"
}
```

### 3. Usar API con token
```
GET http://localhost:8080/api/v1/usuario-service/usuarios
Headers:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 🚨 Errores Comunes

### 401 Unauthorized
- Token expirado o inválido
- No se envió el header `Authorization`
- Credenciales incorrectas

### 400 Bad Request
- Username o correo ya registrado
- Datos de registro incompletos

### 403 Forbidden
- Usuario autenticado pero sin permisos

---

¡Listo! Ahora tu backend tiene autenticación JWT completa y lista para consumir desde Flutter. 🚀
