package co.edu.uceva.celularservice.controller;

import co.edu.uceva.celularservice.model.dto.AuthResponse;
import co.edu.uceva.celularservice.model.dto.LoginRequest;
import co.edu.uceva.celularservice.model.dto.RegisterRequest;
import co.edu.uceva.celularservice.model.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint para login de usuarios
     * @param loginRequest contiene username y password
     * @return JWT token y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Endpoint para registro de nuevos usuarios
     * @param registerRequest contiene username, correo, password y rol
     * @return JWT token y datos del usuario registrado
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse response = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error en el registro");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Endpoint de prueba para verificar autenticación
     * Requiere token JWT válido
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "¡Autenticación exitosa!");
        response.put("status", "authenticated");
        return ResponseEntity.ok(response);
    }
}
