package co.edu.uceva.celularservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String correo;
    private String rol;

    public AuthResponse(String token, Long id, String username, String correo, String rol) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.correo = correo;
        this.rol = rol;
    }
}
