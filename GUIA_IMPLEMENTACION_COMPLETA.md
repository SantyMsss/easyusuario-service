# 🎯 Guía de Implementación Completa - Reconocimiento Facial KYC

## 📋 Índice
1. [Requisitos Previos](#requisitos-previos)
2. [Instalación del Servicio Python](#instalación-del-servicio-python)
3. [Configuración de Spring Boot](#configuración-de-spring-boot)
4. [Implementación en Flutter](#implementación-en-flutter)
5. [Pruebas y Validación](#pruebas-y-validación)
6. [Troubleshooting](#troubleshooting)

---

## 📌 Requisitos Previos

### Software Necesario
- ✅ **Python 3.8+** ([Descargar](https://www.python.org/downloads/))
- ✅ **Java 17+** (ya instalado)
- ✅ **Maven** (ya instalado)
- ✅ **PostgreSQL** (ya instalado)
- ✅ **Flutter SDK** (para el cliente móvil)

### Verificar Instalaciones
```powershell
# Python
python --version

# Java
java -version

# Maven
mvn -version

# Flutter
flutter --version
```

---

## 🐍 PASO 1: Instalación del Servicio Python

### 1.1 Navegar al directorio del servicio
```powershell
cd "C:\Users\USER\Desktop\ING SISTEMAS\7\ING SOFTWARE 2\usuario-service\face-recognition-service"
```

### 1.2 Crear entorno virtual
```powershell
python -m venv venv
```

### 1.3 Activar entorno virtual
```powershell
# Windows PowerShell
.\venv\Scripts\Activate.ps1

# Si hay error de permisos, ejecutar primero:
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### 1.4 Instalar dependencias
```powershell
pip install --upgrade pip
pip install -r requirements.txt
```

**Nota:** La primera vez que ejecutes DeepFace, descargará modelos (~200MB). Esto es normal.

### 1.5 Verificar instalación
```powershell
python -c "import deepface; print('DeepFace instalado correctamente')"
```

### 1.6 Iniciar el servicio
```powershell
python main.py
```

**Salida esperada:**
```
INFO:     Started server process [12345]
INFO:     Waiting for application startup.
INFO:     Application startup complete.
INFO:     Uvicorn running on http://0.0.0.0:5000
```

### 1.7 Probar el servicio (en otra terminal)
```powershell
# Health check
curl http://localhost:5000/health
```

**Respuesta esperada:**
```json
{"status":"healthy","service":"face-recognition"}
```

### 1.8 Ver documentación interactiva
Abre en tu navegador: `http://localhost:5000/docs`

---

## ☕ PASO 2: Configuración de Spring Boot

### 2.1 Verificar estructura de archivos

Asegúrate de que estos archivos existan:

```
usuario-service/
├── src/main/java/.../
│   ├── model/
│   │   ├── entities/
│   │   │   └── FaceEncoding.java ✅
│   │   ├── dao/
│   │   │   └── FaceEncodingDao.java ✅
│   │   ├── dto/
│   │   │   ├── RegisterFaceRequest.java ✅
│   │   │   ├── FaceLoginRequest.java ✅
│   │   │   ├── DeepFaceResponse.java ✅
│   │   │   └── FaceComparisonResponse.java ✅
│   │   └── service/
│   │       ├── IFaceEncodingService.java ✅
│   │       ├── FaceEncodingServiceImpl.java ✅
│   │       ├── DeepFaceClient.java ✅
│   │       └── AuthService.java ✅ (actualizado)
│   ├── controller/
│   │   └── AuthController.java ✅ (actualizado)
│   └── ...
└── src/main/resources/
    ├── application.properties ✅
    └── schema-face-encodings.sql ✅
```

### 2.2 Verificar application.properties

```properties
# Configuracion del servicio DeepFace (Python)
deepface.service.url=http://localhost:5000
```

### 2.3 Compilar el proyecto
```powershell
cd "C:\Users\USER\Desktop\ING SISTEMAS\7\ING SOFTWARE 2\usuario-service"
mvn clean install
```

### 2.4 Ejecutar Spring Boot
```powershell
mvn spring-boot:run
```

**Salida esperada:**
```
Started CelularServiceApplication in X.XXX seconds
```

### 2.5 Verificar conexión entre servicios

Abre otra terminal y ejecuta:
```powershell
curl http://localhost:8080/api/v1/auth/test
```

---

## 📱 PASO 3: Implementación en Flutter

### 3.1 Agregar dependencias en pubspec.yaml

```yaml
dependencies:
  flutter:
    sdk: flutter
  
  # HTTP requests
  http: ^1.1.0
  
  # Cámara
  camera: ^0.10.5+5
  image_picker: ^1.0.4
  
  # Manejo de imágenes
  image: ^4.1.3
  
  # Almacenamiento local (para JWT token)
  shared_preferences: ^2.2.2
  
  # Permisos
  permission_handler: ^11.0.1
```

Instalar dependencias:
```bash
flutter pub get
```

### 3.2 Configurar permisos

**Android** (`android/app/src/main/AndroidManifest.xml`):
```xml
<manifest>
    <!-- Permisos de cámara -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />
    
    <!-- Permisos de almacenamiento -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    
    <!-- Internet -->
    <uses-permission android:name="android.permission.INTERNET"/>
    
    <application>
        ...
    </application>
</manifest>
```

**iOS** (`ios/Runner/Info.plist`):
```xml
<dict>
    <key>NSCameraUsageDescription</key>
    <string>Necesitamos acceso a la cámara para el reconocimiento facial</string>
    <key>NSPhotoLibraryUsageDescription</key>
    <string>Necesitamos acceso a la galería para seleccionar fotos</string>
</dict>
```

### 3.3 Crear el Servicio de Autenticación Facial

**Archivo:** `lib/services/face_auth_service.dart`

```dart
import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

class FaceAuthService {
  // IMPORTANTE: Cambiar según tu configuración
  static const String baseUrl = 'http://10.0.2.2:8080/api/v1/auth'; // Android Emulator
  // static const String baseUrl = 'http://localhost:8080/api/v1/auth'; // iOS Simulator
  // static const String baseUrl = 'http://192.168.1.X:8080/api/v1/auth'; // Dispositivo físico
  
  /// Registro de usuario con reconocimiento facial
  Future<Map<String, dynamic>> registerWithFace({
    required String username,
    required String correo,
    required String password,
    required File imageFile,
    String rol = 'USER',
  }) async {
    try {
      // Convertir imagen a Base64
      final bytes = await imageFile.readAsBytes();
      final base64Image = base64Encode(bytes);
      
      final response = await http.post(
        Uri.parse('$baseUrl/register-face'),
        headers: {
          'Content-Type': 'application/json',
        },
        body: jsonEncode({
          'username': username,
          'correo': correo,
          'password': password,
          'rol': rol,
          'faceImageBase64': 'data:image/jpeg;base64,$base64Image',
        }),
      );
      
      if (response.statusCode == 201) {
        return jsonDecode(response.body);
      } else {
        final error = jsonDecode(response.body);
        throw Exception(error['message'] ?? 'Error en el registro');
      }
    } catch (e) {
      throw Exception('Error de conexión: $e');
    }
  }
  
  /// Login de usuario con reconocimiento facial
  Future<Map<String, dynamic>> loginWithFace({
    String? username,
    required File imageFile,
  }) async {
    try {
      // Convertir imagen a Base64
      final bytes = await imageFile.readAsBytes();
      final base64Image = base64Encode(bytes);
      
      final requestBody = <String, dynamic>{
        'faceImageBase64': 'data:image/jpeg;base64,$base64Image',
      };
      
      // Agregar username si está disponible
      if (username != null && username.isNotEmpty) {
        requestBody['username'] = username;
      }
      
      final response = await http.post(
        Uri.parse('$baseUrl/login-face'),
        headers: {
          'Content-Type': 'application/json',
        },
        body: jsonEncode(requestBody),
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['data']; // Extraer la data del objeto de respuesta
      } else {
        final error = jsonDecode(response.body);
        throw Exception(error['message'] ?? 'Error en la autenticación');
      }
    } catch (e) {
      throw Exception('Error de conexión: $e');
    }
  }
  
  /// Login tradicional con username y password
  Future<Map<String, dynamic>> loginTraditional({
    required String username,
    required String password,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/login'),
        headers: {
          'Content-Type': 'application/json',
        },
        body: jsonEncode({
          'username': username,
          'password': password,
        }),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        final error = jsonDecode(response.body);
        throw Exception(error['message'] ?? 'Credenciales inválidas');
      }
    } catch (e) {
      throw Exception('Error de conexión: $e');
    }
  }
  
  /// Registro tradicional
  Future<Map<String, dynamic>> registerTraditional({
    required String username,
    required String correo,
    required String password,
    String rol = 'USER',
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/register'),
        headers: {
          'Content-Type': 'application/json',
        },
        body: jsonEncode({
          'username': username,
          'correo': correo,
          'password': password,
          'rol': rol,
        }),
      );
      
      if (response.statusCode == 201) {
        return jsonDecode(response.body);
      } else {
        final error = jsonDecode(response.body);
        throw Exception(error['message'] ?? 'Error en el registro');
      }
    } catch (e) {
      throw Exception('Error de conexión: $e');
    }
  }
}
```

### 3.4 Crear Widget de Captura Facial

**Archivo:** `lib/widgets/face_capture_widget.dart`

```dart
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:camera/camera.dart';
import 'package:image_picker/image_picker.dart';

class FaceCaptureWidget extends StatefulWidget {
  final Function(File) onImageCaptured;
  
  const FaceCaptureWidget({
    Key? key,
    required this.onImageCaptured,
  }) : super(key: key);
  
  @override
  State<FaceCaptureWidget> createState() => _FaceCaptureWidgetState();
}

class _FaceCaptureWidgetState extends State<FaceCaptureWidget> {
  CameraController? _controller;
  List<CameraDescription>? _cameras;
  bool _isInitialized = false;
  final ImagePicker _picker = ImagePicker();
  
  @override
  void initState() {
    super.initState();
    _initCamera();
  }
  
  Future<void> _initCamera() async {
    try {
      _cameras = await availableCameras();
      
      // Buscar cámara frontal
      final frontCamera = _cameras!.firstWhere(
        (camera) => camera.lensDirection == CameraLensDirection.front,
        orElse: () => _cameras!.first,
      );
      
      _controller = CameraController(
        frontCamera,
        ResolutionPreset.high,
        enableAudio: false,
      );
      
      await _controller!.initialize();
      
      if (mounted) {
        setState(() {
          _isInitialized = true;
        });
      }
    } catch (e) {
      print('Error al inicializar cámara: $e');
    }
  }
  
  Future<void> _takePicture() async {
    if (_controller == null || !_controller!.value.isInitialized) {
      return;
    }
    
    try {
      final image = await _controller!.takePicture();
      widget.onImageCaptured(File(image.path));
    } catch (e) {
      print('Error al capturar imagen: $e');
    }
  }
  
  Future<void> _pickImageFromGallery() async {
    try {
      final XFile? image = await _picker.pickImage(
        source: ImageSource.gallery,
        imageQuality: 85,
      );
      
      if (image != null) {
        widget.onImageCaptured(File(image.path));
      }
    } catch (e) {
      print('Error al seleccionar imagen: $e');
    }
  }
  
  @override
  Widget build(BuildContext context) {
    if (!_isInitialized || _controller == null) {
      return const Center(
        child: CircularProgressIndicator(),
      );
    }
    
    return Column(
      children: [
        // Vista previa de la cámara
        Expanded(
          child: ClipRRect(
            borderRadius: BorderRadius.circular(20),
            child: CameraPreview(_controller!),
          ),
        ),
        
        const SizedBox(height: 20),
        
        // Instrucciones
        const Text(
          'Centra tu rostro en el recuadro',
          style: TextStyle(fontSize: 16),
        ),
        const SizedBox(height: 10),
        const Text(
          '• Buena iluminación\n• Rostro completo visible\n• Sin accesorios',
          textAlign: TextAlign.center,
          style: TextStyle(fontSize: 12, color: Colors.grey),
        ),
        
        const SizedBox(height: 30),
        
        // Botones
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            // Botón galería
            FloatingActionButton(
              heroTag: 'gallery',
              onPressed: _pickImageFromGallery,
              child: const Icon(Icons.photo_library),
            ),
            
            // Botón capturar
            FloatingActionButton.large(
              heroTag: 'capture',
              onPressed: _takePicture,
              child: const Icon(Icons.camera_alt, size: 36),
            ),
            
            // Botón cambiar cámara
            FloatingActionButton(
              heroTag: 'switch',
              onPressed: () {
                // Implementar cambio de cámara si es necesario
              },
              child: const Icon(Icons.switch_camera),
            ),
          ],
        ),
      ],
    );
  }
  
  @override
  void dispose() {
    _controller?.dispose();
    super.dispose();
  }
}
```

### 3.5 Crear Pantalla de Registro con Rostro

**Archivo:** `lib/screens/face_register_screen.dart`

```dart
import 'dart:io';
import 'package:flutter/material.dart';
import '../services/face_auth_service.dart';
import '../widgets/face_capture_widget.dart';

class FaceRegisterScreen extends StatefulWidget {
  const FaceRegisterScreen({Key? key}) : super(key: key);
  
  @override
  State<FaceRegisterScreen> createState() => _FaceRegisterScreenState();
}

class _FaceRegisterScreenState extends State<FaceRegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  final _usernameController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  
  final FaceAuthService _authService = FaceAuthService();
  
  File? _capturedImage;
  bool _isLoading = false;
  int _currentStep = 0;
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Registro con Reconocimiento Facial'),
      ),
      body: Stepper(
        currentStep: _currentStep,
        onStepContinue: _onStepContinue,
        onStepCancel: _onStepCancel,
        steps: [
          // Paso 1: Datos del usuario
          Step(
            title: const Text('Datos de Usuario'),
            content: Form(
              key: _formKey,
              child: Column(
                children: [
                  TextFormField(
                    controller: _usernameController,
                    decoration: const InputDecoration(
                      labelText: 'Nombre de usuario',
                      prefixIcon: Icon(Icons.person),
                    ),
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'Ingrese un nombre de usuario';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    controller: _emailController,
                    decoration: const InputDecoration(
                      labelText: 'Correo electrónico',
                      prefixIcon: Icon(Icons.email),
                    ),
                    keyboardType: TextInputType.emailAddress,
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'Ingrese un correo';
                      }
                      if (!value.contains('@')) {
                        return 'Ingrese un correo válido';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    controller: _passwordController,
                    decoration: const InputDecoration(
                      labelText: 'Contraseña',
                      prefixIcon: Icon(Icons.lock),
                    ),
                    obscureText: true,
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'Ingrese una contraseña';
                      }
                      if (value.length < 6) {
                        return 'La contraseña debe tener al menos 6 caracteres';
                      }
                      return null;
                    },
                  ),
                ],
              ),
            ),
            isActive: _currentStep >= 0,
          ),
          
          // Paso 2: Captura facial
          Step(
            title: const Text('Captura Facial'),
            content: SizedBox(
              height: 500,
              child: _capturedImage == null
                  ? FaceCaptureWidget(
                      onImageCaptured: (image) {
                        setState(() {
                          _capturedImage = image;
                        });
                      },
                    )
                  : Column(
                      children: [
                        Expanded(
                          child: Image.file(_capturedImage!),
                        ),
                        const SizedBox(height: 20),
                        ElevatedButton.icon(
                          onPressed: () {
                            setState(() {
                              _capturedImage = null;
                            });
                          },
                          icon: const Icon(Icons.refresh),
                          label: const Text('Tomar otra foto'),
                        ),
                      ],
                    ),
            ),
            isActive: _currentStep >= 1,
          ),
          
          // Paso 3: Confirmación
          Step(
            title: const Text('Confirmar'),
            content: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'Revisa tu información:',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16),
                ListTile(
                  leading: const Icon(Icons.person),
                  title: Text(_usernameController.text),
                  subtitle: const Text('Usuario'),
                ),
                ListTile(
                  leading: const Icon(Icons.email),
                  title: Text(_emailController.text),
                  subtitle: const Text('Correo'),
                ),
                ListTile(
                  leading: const Icon(Icons.face),
                  title: const Text('Imagen facial capturada'),
                  subtitle: Text(_capturedImage != null ? '✓ Listo' : '✗ Falta'),
                ),
                const SizedBox(height: 20),
                if (_isLoading)
                  const Center(child: CircularProgressIndicator())
                else
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: _register,
                      icon: const Icon(Icons.check),
                      label: const Text('Registrarse'),
                      style: ElevatedButton.styleFrom(
                        padding: const EdgeInsets.all(16),
                      ),
                    ),
                  ),
              ],
            ),
            isActive: _currentStep >= 2,
          ),
        ],
      ),
    );
  }
  
  void _onStepContinue() {
    if (_currentStep == 0) {
      if (_formKey.currentState!.validate()) {
        setState(() {
          _currentStep++;
        });
      }
    } else if (_currentStep == 1) {
      if (_capturedImage != null) {
        setState(() {
          _currentStep++;
        });
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Por favor, captura tu rostro')),
        );
      }
    }
  }
  
  void _onStepCancel() {
    if (_currentStep > 0) {
      setState(() {
        _currentStep--;
      });
    }
  }
  
  Future<void> _register() async {
    if (_capturedImage == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Falta la imagen facial')),
      );
      return;
    }
    
    setState(() {
      _isLoading = true;
    });
    
    try {
      final result = await _authService.registerWithFace(
        username: _usernameController.text,
        correo: _emailController.text,
        password: _passwordController.text,
        imageFile: _capturedImage!,
      );
      
      if (mounted) {
        // Guardar token
        final token = result['token'];
        // TODO: Guardar en SharedPreferences
        
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('¡Registro exitoso!'),
            backgroundColor: Colors.green,
          ),
        );
        
        // Navegar al home
        Navigator.of(context).pushReplacementNamed('/home');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Error: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }
  
  @override
  void dispose() {
    _usernameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }
}
```

### 3.6 Crear Pantalla de Login Facial

**Archivo:** `lib/screens/face_login_screen.dart`

```dart
import 'dart:io';
import 'package:flutter/material.dart';
import '../services/face_auth_service.dart';
import '../widgets/face_capture_widget.dart';

class FaceLoginScreen extends StatefulWidget {
  const FaceLoginScreen({Key? key}) : super(key: key);
  
  @override
  State<FaceLoginScreen> createState() => _FaceLoginScreenState();
}

class _FaceLoginScreenState extends State<FaceLoginScreen> {
  final _usernameController = TextEditingController();
  final FaceAuthService _authService = FaceAuthService();
  
  File? _capturedImage;
  bool _isLoading = false;
  bool _showUsernameField = true;
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Login Facial'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            // Opción de usar username
            SwitchListTile(
              title: const Text('Especificar usuario'),
              subtitle: const Text('Más rápido si conoces tu username'),
              value: _showUsernameField,
              onChanged: (value) {
                setState(() {
                  _showUsernameField = value;
                });
              },
            ),
            
            const SizedBox(height: 16),
            
            // Campo de username (opcional)
            if (_showUsernameField)
              TextField(
                controller: _usernameController,
                decoration: const InputDecoration(
                  labelText: 'Nombre de usuario (opcional)',
                  prefixIcon: Icon(Icons.person),
                  border: OutlineInputBorder(),
                ),
              ),
            
            const SizedBox(height: 20),
            
            // Captura facial
            Expanded(
              child: _capturedImage == null
                  ? FaceCaptureWidget(
                      onImageCaptured: (image) {
                        setState(() {
                          _capturedImage = image;
                        });
                      },
                    )
                  : Column(
                      children: [
                        Expanded(
                          child: ClipRRect(
                            borderRadius: BorderRadius.circular(20),
                            child: Image.file(
                              _capturedImage!,
                              fit: BoxFit.cover,
                            ),
                          ),
                        ),
                        const SizedBox(height: 20),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            ElevatedButton.icon(
                              onPressed: () {
                                setState(() {
                                  _capturedImage = null;
                                });
                              },
                              icon: const Icon(Icons.refresh),
                              label: const Text('Tomar otra'),
                            ),
                            ElevatedButton.icon(
                              onPressed: _isLoading ? null : _login,
                              icon: _isLoading
                                  ? const SizedBox(
                                      width: 20,
                                      height: 20,
                                      child: CircularProgressIndicator(
                                        strokeWidth: 2,
                                        color: Colors.white,
                                      ),
                                    )
                                  : const Icon(Icons.login),
                              label: const Text('Iniciar Sesión'),
                              style: ElevatedButton.styleFrom(
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 24,
                                  vertical: 12,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
            ),
          ],
        ),
      ),
    );
  }
  
  Future<void> _login() async {
    if (_capturedImage == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Por favor, captura tu rostro')),
      );
      return;
    }
    
    setState(() {
      _isLoading = true;
    });
    
    try {
      final username = _showUsernameField && _usernameController.text.isNotEmpty
          ? _usernameController.text
          : null;
      
      final result = await _authService.loginWithFace(
        username: username,
        imageFile: _capturedImage!,
      );
      
      if (mounted) {
        // Guardar token
        final token = result['token'];
        // TODO: Guardar en SharedPreferences
        
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('¡Bienvenido ${result['username']}!'),
            backgroundColor: Colors.green,
          ),
        );
        
        // Navegar al home
        Navigator.of(context).pushReplacementNamed('/home');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Error: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }
  
  @override
  void dispose() {
    _usernameController.dispose();
    super.dispose();
  }
}
```

---

## 🧪 PASO 4: Pruebas y Validación

### 4.1 Probar Servicio Python

```powershell
# Terminal 1: Ejecutar servicio Python
cd face-recognition-service
.\venv\Scripts\Activate.ps1
python main.py
```

### 4.2 Probar Spring Boot

```powershell
# Terminal 2: Ejecutar Spring Boot
cd ..
mvn spring-boot:run
```

### 4.3 Probar con Postman/Thunder Client

**Test 1: Health Check Python**
```http
GET http://localhost:5000/health
```

**Test 2: Health Check Spring Boot**
```http
GET http://localhost:8080/api/v1/auth/test
```

**Test 3: Registro con Rostro (necesitas una imagen en Base64)**
```http
POST http://localhost:8080/api/v1/auth/register-face
Content-Type: application/json

{
  "username": "test_user",
  "correo": "test@example.com",
  "password": "test123",
  "rol": "USER",
  "faceImageBase64": "data:image/jpeg;base64,/9j/4AAQ..."
}
```

### 4.4 Probar Flutter App

```bash
# Ejecutar en emulador Android
flutter run

# O en dispositivo físico conectado
flutter run --release
```

---

## 🔧 PASO 5: Troubleshooting

### Problema: "El servicio de reconocimiento facial no está disponible"

**Solución:**
1. Verifica que el servicio Python esté ejecutándose:
```powershell
curl http://localhost:5000/health
```

2. Verifica la URL en `application.properties`:
```properties
deepface.service.url=http://localhost:5000
```

### Problema: Error de módulo en Python

**Solución:**
```powershell
pip install --upgrade deepface opencv-python tensorflow
```

### Problema: Flutter no se conecta al backend

**Solución:**

Para **Android Emulator**, usa:
```dart
static const String baseUrl = 'http://10.0.2.2:8080/api/v1/auth';
```

Para **iOS Simulator**, usa:
```dart
static const String baseUrl = 'http://localhost:8080/api/v1/auth';
```

Para **Dispositivo físico**, usa la IP de tu computadora:
```dart
static const String baseUrl = 'http://192.168.1.X:8080/api/v1/auth';
```

Para obtener tu IP:
```powershell
ipconfig
# Buscar "Dirección IPv4"
```

### Problema: "No se detectó ningún rostro"

**Solución:**
- Asegura buena iluminación
- Rostro completo en el encuadre
- Sin lentes oscuros o máscaras
- Fondo uniforme
- Cámara enfocada

### Problema: Error de permisos de cámara en Flutter

**Solución:**
```dart
import 'package:permission_handler/permission_handler.dart';

Future<void> requestCameraPermission() async {
  final status = await Permission.camera.request();
  if (!status.isGranted) {
    // Mostrar mensaje al usuario
  }
}
```

---

## 📊 PASO 6: Configuración Avanzada

### Ajustar Umbral de Similitud

En `AuthService.java`, modifica:
```java
if (comparison.getVerified() && comparison.getSimilarity() >= 70.0) {
    // Cambiar 70.0 a:
    // 80.0 para mayor seguridad
    // 60.0 para menor seguridad
}
```

### Cambiar Modelo DeepFace

En `DeepFaceClient.java`, cambia:
```java
requestBody.put("model_name", "Facenet512"); // Cambia a "VGG-Face", "ArcFace", etc.
```

### Habilitar HTTPS en Producción

1. Obtén certificado SSL
2. Configura en `application.properties`:
```properties
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=tu_password
server.ssl.key-store-type=PKCS12
```

---

## 📝 Checklist Final

- [ ] Python instalado y funcionando
- [ ] Servicio DeepFace ejecutándose en puerto 5000
- [ ] Spring Boot ejecutándose en puerto 8080
- [ ] Base de datos PostgreSQL activa
- [ ] Tabla `face_encodings` creada
- [ ] Flutter configurado con dependencias
- [ ] Permisos de cámara configurados
- [ ] URL correcta en Flutter según dispositivo
- [ ] Prueba de registro exitosa
- [ ] Prueba de login exitosa

---

## 🎉 ¡Listo!

Ahora tienes un sistema completo de autenticación facial tipo KYC funcionando entre:
- **Python (DeepFace)** → Procesamiento facial
- **Spring Boot** → API REST
- **Flutter** → Cliente móvil
- **PostgreSQL** → Almacenamiento

## 📚 Documentación Adicional

- [JWT_AUTHENTICATION_GUIDE.md](JWT_AUTHENTICATION_GUIDE.md)
- [README_FACE_RECOGNITION.md](README_FACE_RECOGNITION.md)
- [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

## 💡 Próximos Pasos

1. Implementar **liveness detection** (detectar persona real)
2. Agregar **logs de auditoría** de autenticaciones
3. Implementar **renovación de tokens**
4. Agregar **autenticación de dos factores (2FA)**
5. Optimizar imágenes antes de enviar

---

**Desarrollado para:** EasySave Service  
**Fecha:** Noviembre 2025  
**Versión:** 1.0.0
