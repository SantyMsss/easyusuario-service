package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.dto.DeepFaceResponse;
import co.edu.uceva.celularservice.model.dto.FaceComparisonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Cliente REST para comunicarse con el microservicio Python DeepFace
 */
@Service
public class DeepFaceClient {

    private static final Logger log = LoggerFactory.getLogger(DeepFaceClient.class);

    @Value("${deepface.service.url:http://localhost:5000}")
    private String deepFaceServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DeepFaceClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Genera el embedding facial de una imagen
     * 
     * @param imageBase64 Imagen en Base64
     * @param modelName Modelo a utilizar (Facenet512, VGG-Face, ArcFace, etc.)
     * @return DeepFaceResponse con el embedding generado
     */
    public DeepFaceResponse encodeFace(String imageBase64, String modelName) {
        DeepFaceResponse errorResponse = new DeepFaceResponse();
        errorResponse.setFaceDetected(false);
        errorResponse.setConfidence(0.0);
        
        try {
            log.info("Iniciando encode de rostro con modelo: {}", modelName);
            String url = deepFaceServiceUrl + "/encode-face";

            // Preparar request
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("image_base64", imageBase64);
            requestBody.put("model_name", modelName != null ? modelName : "Facenet512");
            requestBody.put("detector_backend", "opencv");

            log.debug("URL del servicio: {}", url);
            log.debug("Longitud de imagen Base64: {} caracteres", imageBase64 != null ? imageBase64.length() : 0);

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // Realizar petición
            log.info("Enviando petición a DeepFace service...");
            ResponseEntity<DeepFaceResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    DeepFaceResponse.class
            );

            log.info("Respuesta recibida con status: {}", response.getStatusCode());
            DeepFaceResponse result = response.getBody();
            
            // Validar respuesta
            if (result == null) {
                log.error("El servicio DeepFace devolvió una respuesta vacía");
                errorResponse.setError("El servicio DeepFace devolvió una respuesta vacía");
                return errorResponse;
            }
            
            // Asegurar que los valores no sean null
            if (result.getFaceDetected() == null) {
                log.warn("faceDetected es null, estableciendo a false");
                result.setFaceDetected(false);
            }
            if (result.getConfidence() == null) {
                log.warn("confidence es null, estableciendo a 0.0");
                result.setConfidence(0.0);
            }
            
            log.info("Rostro detectado: {}, Confianza: {}", result.getFaceDetected(), result.getConfidence());
            return result;

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Error del servidor Python (400, 404, etc.)
            log.error("Error HTTP del servicio DeepFace: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            errorResponse.setError("Error en el servicio DeepFace: " + e.getResponseBodyAsString());
            return errorResponse;
        } catch (org.springframework.web.client.ResourceAccessException e) {
            // Servicio no disponible
            log.error("No se pudo conectar al servicio DeepFace en: {}", deepFaceServiceUrl, e);
            errorResponse.setError("No se pudo conectar al servicio DeepFace. Verifica que esté ejecutándose en " + deepFaceServiceUrl);
            return errorResponse;
        } catch (Exception e) {
            // Otros errores
            log.error("Error inesperado al comunicarse con DeepFace", e);
            errorResponse.setError("Error inesperado al comunicarse con el servicio DeepFace: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * Compara dos imágenes faciales
     * 
     * @param image1Base64 Primera imagen en Base64
     * @param image2Base64 Segunda imagen en Base64
     * @param modelName Modelo a utilizar
     * @param distanceMetric Métrica de distancia (cosine, euclidean, euclidean_l2)
     * @return FaceComparisonResponse con el resultado de la comparación
     */
    public FaceComparisonResponse compareFaces(String image1Base64, String image2Base64, 
                                               String modelName, String distanceMetric) {
        try {
            String url = deepFaceServiceUrl + "/compare-faces";

            // Preparar request
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("image1_base64", image1Base64);
            requestBody.put("image2_base64", image2Base64);
            requestBody.put("model_name", modelName != null ? modelName : "Facenet512");
            requestBody.put("distance_metric", distanceMetric != null ? distanceMetric : "cosine");
            requestBody.put("detector_backend", "opencv");

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // Realizar petición
            ResponseEntity<FaceComparisonResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    FaceComparisonResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            // Retornar respuesta de error
            FaceComparisonResponse errorResponse = new FaceComparisonResponse();
            errorResponse.setVerified(false);
            return errorResponse;
        }
    }

    /**
     * Verifica una imagen contra un embedding almacenado
     * 
     * @param imageBase64 Imagen a verificar en Base64
     * @param storedEmbedding Embedding almacenado (JSON array como string)
     * @param modelName Modelo utilizado
     * @param distanceMetric Métrica de distancia
     * @return FaceComparisonResponse con el resultado
     */
    public FaceComparisonResponse verifyFace(String imageBase64, String storedEmbedding,
                                             String modelName, String distanceMetric) {
        try {
            String url = deepFaceServiceUrl + "/verify-face";

            // Preparar request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("image_base64", imageBase64);
            requestBody.put("stored_embedding", storedEmbedding);
            requestBody.put("model_name", modelName != null ? modelName : "Facenet512");
            requestBody.put("distance_metric", distanceMetric != null ? distanceMetric : "cosine");
            requestBody.put("detector_backend", "opencv");

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Realizar petición
            ResponseEntity<FaceComparisonResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    FaceComparisonResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            // Retornar respuesta de error
            FaceComparisonResponse errorResponse = new FaceComparisonResponse();
            errorResponse.setVerified(false);
            return errorResponse;
        }
    }

    /**
     * Verifica la salud del servicio DeepFace
     * 
     * @return true si el servicio está activo
     */
    public boolean isServiceHealthy() {
        try {
            String url = deepFaceServiceUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
}
