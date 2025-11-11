"""
Microservicio de Reconocimiento Facial con DeepFace
Proporciona endpoints para encoding y comparación facial
"""

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from deepface import DeepFace
import base64
import numpy as np
import cv2
import json
import os
from typing import Optional

app = FastAPI(
    title="Face Recognition Service",
    description="Servicio de reconocimiento facial usando DeepFace",
    version="1.0.0"
)

# Configurar CORS para permitir peticiones desde Spring Boot
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # En producción, especificar el dominio de Spring Boot
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Modelos de entrada
class ImageRequest(BaseModel):
    image_base64: str
    model_name: Optional[str] = "Facenet512"  # Facenet512, VGG-Face, ArcFace, OpenFace, DeepFace, DeepID, Dlib, SFace
    detector_backend: Optional[str] = "opencv"  # opencv, ssd, dlib, mtcnn, retinaface

class CompareRequest(BaseModel):
    image1_base64: str
    image2_base64: str
    model_name: Optional[str] = "Facenet512"
    distance_metric: Optional[str] = "cosine"  # cosine, euclidean, euclidean_l2
    detector_backend: Optional[str] = "opencv"

class EmbeddingCompareRequest(BaseModel):
    image_base64: str
    stored_embedding: str  # JSON array como string
    model_name: Optional[str] = "Facenet512"
    distance_metric: Optional[str] = "cosine"
    threshold: Optional[float] = None


# Funciones auxiliares
def base64_to_image(base64_string: str):
    """Convierte una imagen Base64 a formato OpenCV"""
    try:
        # Remover prefijo si existe (data:image/png;base64,)
        if ',' in base64_string:
            base64_string = base64_string.split(',')[1]
        
        img_data = base64.b64decode(base64_string)
        nparr = np.frombuffer(img_data, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        
        if img is None:
            raise ValueError("No se pudo decodificar la imagen")
        
        return img
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Error al procesar imagen: {str(e)}")


def save_temp_image(img, prefix="temp"):
    """Guarda imagen temporal y retorna la ruta"""
    temp_dir = "temp_images"
    os.makedirs(temp_dir, exist_ok=True)
    
    temp_path = os.path.join(temp_dir, f"{prefix}_{np.random.randint(100000)}.jpg")
    cv2.imwrite(temp_path, img)
    return temp_path


def calculate_similarity_percentage(distance: float, threshold: float) -> float:
    """Calcula porcentaje de similitud basado en la distancia"""
    if distance <= threshold:
        # Si está dentro del umbral, calcular porcentaje
        similarity = (1 - (distance / threshold)) * 100
        return min(100, max(0, similarity))
    else:
        # Si excede el umbral
        return max(0, (1 - distance) * 100)


# Endpoints
@app.get("/")
def root():
    """Endpoint raíz para verificar que el servicio está activo"""
    return {
        "service": "Face Recognition Service",
        "status": "active",
        "version": "1.0.0",
        "endpoints": {
            "encode": "/encode-face",
            "compare": "/compare-faces",
            "verify": "/verify-face",
            "health": "/health"
        }
    }


@app.get("/health")
def health_check():
    """Endpoint de health check"""
    return {"status": "healthy", "service": "face-recognition"}


@app.post("/encode-face")
async def encode_face(request: ImageRequest):
    """
    Genera el embedding facial de una imagen
    
    Returns:
        - embedding: Vector de características faciales (JSON array)
        - model: Modelo utilizado
        - confidence: Confianza de la detección
        - face_detected: Si se detectó un rostro
    """
    temp_path = None
    try:
        # Convertir Base64 a imagen
        img = base64_to_image(request.image_base64)
        
        # Guardar temporalmente
        temp_path = save_temp_image(img, "encode")
        
        # Generar embedding con DeepFace
        embedding_objs = DeepFace.represent(
            img_path=temp_path,
            model_name=request.model_name,
            detector_backend=request.detector_backend,
            enforce_detection=True
        )
        
        if not embedding_objs or len(embedding_objs) == 0:
            return {
                "face_detected": False,
                "error": "No se detectó ningún rostro en la imagen",
                "embedding": None,
                "model": request.model_name,
                "confidence": 0.0
            }
        
        # Tomar el primer rostro detectado
        embedding_obj = embedding_objs[0]
        embedding = embedding_obj["embedding"]
        
        # Obtener información de confianza si está disponible
        confidence = embedding_obj.get("facial_area", {}).get("confidence", 1.0)
        
        return {
            "embedding": json.dumps(embedding),
            "model": request.model_name,
            "face_detected": True,
            "confidence": float(confidence),
            "error": None
        }
        
    except ValueError as e:
        # No se detectó rostro
        return {
            "face_detected": False,
            "error": f"No se detectó rostro: {str(e)}",
            "embedding": None,
            "model": request.model_name,
            "confidence": 0.0
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al generar embedding: {str(e)}")
    finally:
        # Limpiar archivo temporal
        if temp_path and os.path.exists(temp_path):
            os.remove(temp_path)


@app.post("/compare-faces")
async def compare_faces(request: CompareRequest):
    """
    Compara dos imágenes faciales directamente
    
    Returns:
        - verified: Si las caras coinciden
        - distance: Distancia entre los embeddings
        - threshold: Umbral utilizado
        - similarity: Porcentaje de similitud
    """
    temp_path1 = None
    temp_path2 = None
    
    try:
        # Convertir imágenes Base64
        img1 = base64_to_image(request.image1_base64)
        img2 = base64_to_image(request.image2_base64)
        
        # Guardar temporalmente
        temp_path1 = save_temp_image(img1, "compare1")
        temp_path2 = save_temp_image(img2, "compare2")
        
        # Comparar con DeepFace
        result = DeepFace.verify(
            img1_path=temp_path1,
            img2_path=temp_path2,
            model_name=request.model_name,
            distance_metric=request.distance_metric,
            detector_backend=request.detector_backend,
            enforce_detection=True
        )
        
        distance = result["distance"]
        threshold = result["threshold"]
        verified = result["verified"]
        
        # Calcular porcentaje de similitud
        similarity = calculate_similarity_percentage(distance, threshold)
        
        return {
            "verified": bool(verified),
            "distance": float(distance),
            "threshold": float(threshold),
            "model": request.model_name,
            "distance_metric": request.distance_metric,
            "similarity": round(similarity, 2)
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al comparar rostros: {str(e)}")
    finally:
        # Limpiar archivos temporales
        if temp_path1 and os.path.exists(temp_path1):
            os.remove(temp_path1)
        if temp_path2 and os.path.exists(temp_path2):
            os.remove(temp_path2)


@app.post("/verify-face")
async def verify_face(request: EmbeddingCompareRequest):
    """
    Verifica una imagen contra un embedding almacenado
    
    Returns:
        - verified: Si la cara coincide con el embedding
        - distance: Distancia calculada
        - similarity: Porcentaje de similitud
    """
    temp_path = None
    
    try:
        # Convertir imagen Base64
        img = base64_to_image(request.image_base64)
        temp_path = save_temp_image(img, "verify")
        
        # Generar embedding de la imagen nueva
        new_embedding_objs = DeepFace.represent(
            img_path=temp_path,
            model_name=request.model_name,
            detector_backend=request.detector_backend,
            enforce_detection=True
        )
        
        if not new_embedding_objs:
            return {
                "verified": False,
                "error": "No se detectó rostro en la imagen",
                "distance": None,
                "similarity": 0.0
            }
        
        new_embedding = np.array(new_embedding_objs[0]["embedding"])
        
        # Parsear embedding almacenado
        stored_embedding = np.array(json.loads(request.stored_embedding))
        
        # Calcular distancia
        if request.distance_metric == "cosine":
            distance = 1 - np.dot(new_embedding, stored_embedding) / (
                np.linalg.norm(new_embedding) * np.linalg.norm(stored_embedding)
            )
        elif request.distance_metric == "euclidean":
            distance = np.linalg.norm(new_embedding - stored_embedding)
        elif request.distance_metric == "euclidean_l2":
            distance = np.sqrt(np.sum((new_embedding - stored_embedding) ** 2))
        else:
            distance = 1 - np.dot(new_embedding, stored_embedding) / (
                np.linalg.norm(new_embedding) * np.linalg.norm(stored_embedding)
            )
        
        # Umbrales por defecto según modelo y métrica
        default_thresholds = {
            "Facenet512": {"cosine": 0.30, "euclidean": 23.56, "euclidean_l2": 1.04},
            "VGG-Face": {"cosine": 0.40, "euclidean": 0.60, "euclidean_l2": 0.86},
            "ArcFace": {"cosine": 0.68, "euclidean": 4.15, "euclidean_l2": 1.13},
        }
        
        threshold = request.threshold
        if threshold is None:
            model_thresholds = default_thresholds.get(request.model_name, default_thresholds["Facenet512"])
            threshold = model_thresholds.get(request.distance_metric, 0.30)
        
        verified = distance <= threshold
        similarity = calculate_similarity_percentage(distance, threshold)
        
        return {
            "verified": bool(verified),
            "distance": float(distance),
            "threshold": float(threshold),
            "similarity": round(similarity, 2),
            "model": request.model_name,
            "distance_metric": request.distance_metric
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al verificar rostro: {str(e)}")
    finally:
        if temp_path and os.path.exists(temp_path):
            os.remove(temp_path)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5000)
