# Face Recognition Service - DeepFace

Microservicio de reconocimiento facial usando DeepFace para el sistema EasySave.

## 🚀 Instalación

### 1. Crear entorno virtual
```bash
python -m venv venv
```

### 2. Activar entorno virtual
**Windows:**
```bash
venv\Scripts\activate
```

**Linux/Mac:**
```bash
source venv/bin/activate
```

### 3. Instalar dependencias
```bash
pip install -r requirements.txt
```

## ▶️ Ejecución

```bash
python main.py
```

El servicio estará disponible en: `http://localhost:5000`

## 📚 Documentación API

Una vez ejecutado, accede a:
- Swagger UI: `http://localhost:5000/docs`
- ReDoc: `http://localhost:5000/redoc`

## 🔧 Endpoints

### 1. `/encode-face` (POST)
Genera el embedding facial de una imagen.

**Request:**
```json
{
  "image_base64": "base64_string_aqui",
  "model_name": "Facenet512",
  "detector_backend": "opencv"
}
```

**Response:**
```json
{
  "embedding": "[0.123, 0.456, ...]",
  "model": "Facenet512",
  "face_detected": true,
  "confidence": 0.98
}
```

### 2. `/compare-faces` (POST)
Compara dos imágenes faciales.

**Request:**
```json
{
  "image1_base64": "base64_string_1",
  "image2_base64": "base64_string_2",
  "model_name": "Facenet512",
  "distance_metric": "cosine"
}
```

**Response:**
```json
{
  "verified": true,
  "distance": 0.25,
  "threshold": 0.30,
  "similarity": 85.5
}
```

### 3. `/verify-face` (POST)
Verifica una imagen contra un embedding almacenado.

**Request:**
```json
{
  "image_base64": "base64_string",
  "stored_embedding": "[0.123, 0.456, ...]",
  "model_name": "Facenet512",
  "distance_metric": "cosine"
}
```

**Response:**
```json
{
  "verified": true,
  "distance": 0.22,
  "threshold": 0.30,
  "similarity": 88.3
}
```

## 🤖 Modelos Disponibles

- **Facenet512** (recomendado): Alta precisión, 512 dimensiones
- **VGG-Face**: Modelo clásico, buena precisión
- **ArcFace**: Estado del arte, muy preciso
- **OpenFace**: Rápido, menor precisión
- **DeepFace**: Modelo original de Facebook
- **DeepID**: Modelo alternativo
- **Dlib**: Rápido y ligero
- **SFace**: Optimizado para velocidad

## 📊 Métricas de Distancia

- **cosine** (recomendada): Similitud del coseno
- **euclidean**: Distancia euclidiana
- **euclidean_l2**: Distancia euclidiana L2 normalizada

## 🔍 Detectores de Rostro

- **opencv** (predeterminado): Rápido
- **ssd**: Más preciso
- **dlib**: Buena precisión
- **mtcnn**: Muy preciso
- **retinaface**: Estado del arte

## 🐳 Docker (Opcional)

```dockerfile
FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD ["python", "main.py"]
```

## 📝 Notas

- Las imágenes se procesan temporalmente y se eliminan después
- El servicio guarda archivos temporales en `temp_images/`
- Se recomienda usar HTTPS en producción
- Ajustar los umbrales según las necesidades de seguridad
