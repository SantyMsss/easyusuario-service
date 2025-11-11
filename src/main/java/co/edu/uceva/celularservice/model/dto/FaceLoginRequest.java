package co.edu.uceva.celularservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para login mediante reconocimiento facial
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceLoginRequest {
    /**
     * Imagen facial en Base64 para verificación
     */
    private String faceImageBase64;
    
    /**
     * Username opcional (si se conoce de antemano)
     * Si no se proporciona, se buscará el usuario por comparación facial
     */
    private String username;
}
