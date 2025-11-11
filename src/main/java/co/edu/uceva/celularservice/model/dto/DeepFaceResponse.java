package co.edu.uceva.celularservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta del servicio Python DeepFace
 * Contiene el embedding facial generado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeepFaceResponse {
    /**
     * Embedding facial (vector de características)
     * Representado como JSON array de números
     */
    private String embedding;
    
    /**
     * Modelo utilizado (Facenet512, VGG-Face, ArcFace, etc.)
     */
    private String model;
    
    /**
     * Indica si se detectó un rostro en la imagen
     */
    private Boolean faceDetected;
    
    /**
     * Mensaje de error si algo falló
     */
    private String error;
    
    /**
     * Score de confianza de la detección facial (0-1)
     */
    private Double confidence;
}
