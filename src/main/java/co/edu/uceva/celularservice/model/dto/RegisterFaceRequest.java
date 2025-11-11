package co.edu.uceva.celularservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registro de usuario con reconocimiento facial
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterFaceRequest {
    private String username;
    private String correo;
    private String password;
    private String rol; // "USER" o "ADMIN"
    
    /**
     * Imagen facial en Base64 para generar el embedding
     */
    private String faceImageBase64;
}
