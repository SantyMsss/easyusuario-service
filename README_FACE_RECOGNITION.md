# 🔐 Reconocimiento Facial KYC - EasySave Service

## 📋 Descripción General

Sistema de autenticación biométrica mediante reconocimiento facial implementado con:
- **Spring Boot** (Backend Java)
- **DeepFace** (Python - Procesamiento facial)
- **PostgreSQL** (Base de datos)
- **Flutter** (Cliente móvil)

## 🏗️ Arquitectura

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────────┐
│  Flutter    │ ◄─────► │  Spring Boot     │ ◄─────► │  Python DeepFace│
│    App      │  HTTP   │  API Gateway     │   REST  │    Service      │
└─────────────┘         └──────────────────┘         └─────────────────┘
                               │
                               ▼
                        ┌─────────────┐
                        │ PostgreSQL  │
                        │   Database  │
                        └─────────────┘
```

## 🚀 Instalación y Configuración

### 1. Servicio Python (DeepFace)

```bash
cd face-recognition-service

# Crear entorno virtual
python -m venv venv

# Activar entorno (Windows)
venv\Scripts\activate

# Activar entorno (Linux/Mac)
source venv/bin/activate

# Instalar dependencias
pip install -r requirements.txt

# Ejecutar servicio
python main.py
```

El servicio estará en: `http://localhost:5000`

### 2. Servicio Spring Boot

```bash
# Ya está configurado en application.properties
deepface.service.url=http://localhost:5000

# Ejecutar Spring Boot
mvn spring-boot:run
```

El servicio estará en: `http://localhost:8080`

### 3. Base de Datos

La tabla `face_encodings` se crea automáticamente con JPA. Si deseas crearla manualmente:

```sql
-- Ver: src/main/resources/schema-face-encodings.sql
```

## 📡 Endpoints API

### Registro con Rostro
```http
POST http://localhost:8080/api/v1/auth/register-face
Content-Type: application/json

{
  "username": "maria",
  "correo": "maria@example.com",
  "password": "password123",
  "rol": "USER",
  "faceImageBase64": "data:image/jpeg;base64,/9j/4AAQSkZJRg..."
}
```

### Login con Rostro
```http
POST http://localhost:8080/api/v1/auth/login-face
Content-Type: application/json

{
  "username": "maria",
  "faceImageBase64": "data:image/jpeg;base64,/9j/4AAQSkZJRg..."
}
```

## 📱 Integración con Flutter

### Paquetes Necesarios

```yaml
dependencies:
  camera: ^0.10.0
  image_picker: ^1.0.0
  http: ^1.0.0
  image: ^4.0.0
```

### Ejemplo de Código Flutter

```dart
import 'dart:convert';
import 'dart:io';
import 'package:camera/camera.dart';
import 'package:http/http.dart' as http;

class FaceAuthService {
  final String baseUrl = 'http://localhost:8080/api/v1/auth';

  // Registro con rostro
  Future<Map<String, dynamic>> registerWithFace({
    required String username,
    required String correo,
    required String password,
    required File imageFile,
  }) async {
    // Convertir imagen a Base64
    final bytes = await imageFile.readAsBytes();
    final base64Image = base64Encode(bytes);

    final response = await http.post(
      Uri.parse('$baseUrl/register-face'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'username': username,
        'correo': correo,
        'password': password,
        'rol': 'USER',
        'faceImageBase64': 'data:image/jpeg;base64,$base64Image',
      }),
    );

    if (response.statusCode == 201) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Error en registro: ${response.body}');
    }
  }

  // Login con rostro
  Future<Map<String, dynamic>> loginWithFace({
    String? username,
    required File imageFile,
  }) async {
    final bytes = await imageFile.readAsBytes();
    final base64Image = base64Encode(bytes);

    final response = await http.post(
      Uri.parse('$baseUrl/login-face'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        if (username != null) 'username': username,
        'faceImageBase64': 'data:image/jpeg;base64,$base64Image',
      }),
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Error en login: ${response.body}');
    }
  }

  // Capturar imagen con cámara
  Future<File?> captureImage() async {
    final cameras = await availableCameras();
    final frontCamera = cameras.firstWhere(
      (camera) => camera.lensDirection == CameraLensDirection.front,
    );

    // Implementar UI para captura
    // Retornar File de la imagen capturada
    return null;
  }
}
```

### Widget de Captura de Rostro

```dart
import 'package:flutter/material.dart';
import 'package:camera/camera.dart';

class FaceCaptureWidget extends StatefulWidget {
  @override
  _FaceCaptureWidgetState createState() => _FaceCaptureWidgetState();
}

class _FaceCaptureWidgetState extends State<FaceCaptureWidget> {
  CameraController? controller;
  
  @override
  void initState() {
    super.initState();
    _initCamera();
  }
  
  Future<void> _initCamera() async {
    final cameras = await availableCameras();
    final frontCamera = cameras.firstWhere(
      (camera) => camera.lensDirection == CameraLensDirection.front,
    );
    
    controller = CameraController(
      frontCamera,
      ResolutionPreset.high,
      enableAudio: false,
    );
    
    await controller!.initialize();
    setState(() {});
  }
  
  Future<void> _takePicture() async {
    if (controller == null || !controller!.value.isInitialized) return;
    
    final image = await controller!.takePicture();
    Navigator.pop(context, File(image.path));
  }
  
  @override
  Widget build(BuildContext context) {
    if (controller == null || !controller!.value.isInitialized) {
      return Center(child: CircularProgressIndicator());
    }
    
    return Scaffold(
      body: Stack(
        children: [
          CameraPreview(controller!),
          Align(
            alignment: Alignment.bottomCenter,
            child: Padding(
              padding: EdgeInsets.all(20),
              child: FloatingActionButton(
                onPressed: _takePicture,
                child: Icon(Icons.camera),
              ),
            ),
          ),
        ],
      ),
    );
  }
  
  @override
  void dispose() {
    controller?.dispose();
    super.dispose();
  }
}
```

## 🔧 Modelos y Configuración

### Modelos Disponibles en DeepFace

| Modelo | Dimensiones | Precisión | Velocidad |
|--------|-------------|-----------|-----------|
| **Facenet512** | 512 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| VGG-Face | 2622 | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| ArcFace | 512 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| OpenFace | 128 | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

**Recomendado**: Facenet512 (mejor balance precisión/velocidad)

### Métricas de Distancia

- **cosine** (recomendada): Similitud del coseno (0-1)
- **euclidean**: Distancia euclidiana
- **euclidean_l2**: Distancia euclidiana normalizada

### Umbrales de Verificación

| Modelo | Cosine | Euclidean | Euclidean_L2 |
|--------|--------|-----------|--------------|
| Facenet512 | 0.30 | 23.56 | 1.04 |
| VGG-Face | 0.40 | 0.60 | 0.86 |
| ArcFace | 0.68 | 4.15 | 1.13 |

## 🔐 Seguridad

### Mejores Prácticas

1. **Liveness Detection**: Detectar que es una persona real (no foto/video)
2. **Cifrado**: Almacenar embeddings cifrados en BD
3. **HTTPS**: Siempre usar SSL/TLS en producción
4. **Rate Limiting**: Limitar intentos de autenticación facial
5. **Fallback**: Permitir login tradicional como alternativa
6. **Auditoría**: Registrar intentos de autenticación facial

### Recomendaciones de Imagen

✅ **SÍ**:
- Buena iluminación frontal
- Fondo uniforme
- Rostro completo visible
- Vista frontal directa
- Imagen de alta calidad

❌ **NO**:
- Lentes oscuros
- Máscaras o accesorios que cubran el rostro
- Poca iluminación
- Múltiples personas en la imagen
- Ángulos extremos

## 📊 Base de Datos

### Tabla: face_encodings

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID único |
| usuario_id | BIGINT | FK a usuarios (único) |
| face_embedding | TEXT | Vector de características (JSON) |
| face_image_path | VARCHAR | Ruta de imagen de referencia |
| model_name | VARCHAR | Modelo usado (Facenet512) |
| distance_metric | VARCHAR | Métrica de distancia (cosine) |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |
| is_active | BOOLEAN | Si está activo |

## 🧪 Pruebas

### Probar Servicio Python

```bash
# Health check
curl http://localhost:5000/health

# Encode face (requiere imagen en Base64)
curl -X POST http://localhost:5000/encode-face \
  -H "Content-Type: application/json" \
  -d '{"image_base64": "..."}'
```

### Probar Spring Boot

```bash
# Registro con rostro
curl -X POST http://localhost:8080/api/v1/auth/register-face \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "correo": "test@test.com",
    "password": "test123",
    "faceImageBase64": "..."
  }'

# Login con rostro
curl -X POST http://localhost:8080/api/v1/auth/login-face \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "faceImageBase64": "..."
  }'
```

## 🐛 Solución de Problemas

### Error: "El servicio de reconocimiento facial no está disponible"
**Solución**: Asegúrate de que el servicio Python esté ejecutándose en `http://localhost:5000`

### Error: "No se detectó ningún rostro en la imagen"
**Solución**: 
- Verifica que la imagen tenga buena iluminación
- Asegúrate de que el rostro esté completamente visible
- Prueba con una imagen de mejor calidad

### Error: "La calidad de la imagen facial es baja"
**Solución**: Captura una nueva imagen con mejor iluminación y mayor resolución

### Error de conexión entre Spring Boot y Python
**Solución**: Verifica la configuración en `application.properties`:
```properties
deepface.service.url=http://localhost:5000
```

## 📚 Documentación Adicional

- [JWT Authentication Guide](JWT_AUTHENTICATION_GUIDE.md)
- [API Documentation](API_DOCUMENTATION.md)
- [DeepFace Documentation](https://github.com/serengil/deepface)

## 📞 Soporte

Para problemas o preguntas, consulta la documentación o contacta al equipo de desarrollo.

---

**Versión**: 1.0.0  
**Última actualización**: Noviembre 2025
