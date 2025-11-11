package co.edu.uceva.celularservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para comparación facial
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceComparisonResponse {
    /**
     * Indica si las caras coinciden
     */
    private Boolean verified;
    
    /**
     * Distancia entre los embeddings (menor = más similar)
     */
    private Double distance;
    
    /**
     * Umbral utilizado para la verificación
     */
    private Double threshold;
    
    /**
     * Modelo utilizado
     */
    private String model;
    
    /**
     * Métrica de distancia utilizada (cosine, euclidean, euclidean_l2)
     */
    private String distanceMetric;
    
    /**
     * Porcentaje de similitud (0-100)
     */
    private Double similarity;
}
