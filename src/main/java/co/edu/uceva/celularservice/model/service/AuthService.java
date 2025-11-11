package co.edu.uceva.celularservice.model.service;

import co.edu.uceva.celularservice.model.dao.UsuarioDao;
import co.edu.uceva.celularservice.model.dto.*;
import co.edu.uceva.celularservice.model.entities.FaceEncoding;
import co.edu.uceva.celularservice.model.entities.Usuario;
import co.edu.uceva.celularservice.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private DeepFaceClient deepFaceClient;

    @Autowired
    private FaceEncodingServiceImpl faceEncodingService;

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        Usuario usuario = usuarioDao.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new AuthResponse(
                jwt,
                usuario.getId(),
                usuario.getUsername(),
                usuario.getCorreo(),
                usuario.getRol()
        );
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        // Verificar si el username ya existe
        if (usuarioDao.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("El username ya está en uso");
        }

        // Verificar si el correo ya existe
        if (usuarioDao.existsByCorreo(registerRequest.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(registerRequest.getUsername());
        usuario.setCorreo(registerRequest.getCorreo());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setRol(registerRequest.getRol() != null ? registerRequest.getRol() : "USER");

        usuarioDao.save(usuario);

        // Autenticar y generar token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()
                )
        );

        String jwt = tokenProvider.generateToken(authentication);

        return new AuthResponse(
                jwt,
                usuario.getId(),
                usuario.getUsername(),
                usuario.getCorreo(),
                usuario.getRol()
        );
    }

    /**
     * Registra un usuario con reconocimiento facial
     * 1. Verifica que no exista el usuario
     * 2. Genera el embedding facial usando DeepFace
     * 3. Crea el usuario y almacena el encoding facial
     * 4. Retorna JWT token
     */
    public AuthResponse registerWithFace(RegisterFaceRequest registerRequest) {
        // Verificar si el username ya existe
        if (usuarioDao.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("El username ya está en uso");
        }

        // Verificar si el correo ya existe
        if (usuarioDao.existsByCorreo(registerRequest.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Verificar que el servicio DeepFace esté disponible
        if (!deepFaceClient.isServiceHealthy()) {
            throw new RuntimeException("El servicio de reconocimiento facial no está disponible");
        }

        // Generar embedding facial
        DeepFaceResponse deepFaceResponse = deepFaceClient.encodeFace(
                registerRequest.getFaceImageBase64(),
                "Facenet512"
        );

        // Verificar que se detectó un rostro
        if (deepFaceResponse == null) {
            throw new RuntimeException("No se recibió respuesta del servicio de reconocimiento facial");
        }
        
        if (deepFaceResponse.getFaceDetected() == null || !deepFaceResponse.getFaceDetected()) {
            String errorMsg = "No se detectó ningún rostro en la imagen";
            if (deepFaceResponse.getError() != null && !deepFaceResponse.getError().isEmpty()) {
                errorMsg += ": " + deepFaceResponse.getError();
            }
            throw new RuntimeException(errorMsg);
        }

        // Verificar confianza mínima (opcional)
        Double confidence = deepFaceResponse.getConfidence();
        if (confidence != null && confidence < 0.90) {
            throw new RuntimeException("La calidad de la imagen facial es baja (" + 
                    String.format("%.1f%%", confidence * 100) + 
                    "). Por favor, intente con mejor iluminación.");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(registerRequest.getUsername());
        usuario.setCorreo(registerRequest.getCorreo());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setRol(registerRequest.getRol() != null ? registerRequest.getRol() : "USER");

        usuarioDao.save(usuario);

        // Crear y guardar FaceEncoding
        FaceEncoding faceEncoding = new FaceEncoding();
        faceEncoding.setUsuario(usuario);
        faceEncoding.setFaceEmbedding(deepFaceResponse.getEmbedding());
        faceEncoding.setModelName(deepFaceResponse.getModel());
        faceEncoding.setDistanceMetric("cosine");
        faceEncoding.setIsActive(true);

        faceEncodingService.save(faceEncoding);

        // Autenticar y generar token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()
                )
        );

        String jwt = tokenProvider.generateToken(authentication);

        return new AuthResponse(
                jwt,
                usuario.getId(),
                usuario.getUsername(),
                usuario.getCorreo(),
                usuario.getRol()
        );
    }

    /**
     * Login mediante reconocimiento facial
     * 1. Genera embedding de la imagen proporcionada
     * 2. Si se proporciona username, verifica contra ese usuario
     * 3. Si no, busca en todos los usuarios activos con face encoding
     * 4. Retorna JWT token si la verificación es exitosa
     */
    public AuthResponse loginWithFace(FaceLoginRequest faceLoginRequest) {
        // Verificar que el servicio DeepFace esté disponible
        if (!deepFaceClient.isServiceHealthy()) {
            throw new RuntimeException("El servicio de reconocimiento facial no está disponible");
        }

        Usuario usuarioAutenticado = null;
        Double maxSimilarity = 0.0;

        if (faceLoginRequest.getUsername() != null && !faceLoginRequest.getUsername().isEmpty()) {
            // Caso 1: Username proporcionado - verificar solo ese usuario
            Usuario usuario = usuarioDao.findByUsername(faceLoginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            FaceEncoding faceEncoding = faceEncodingService.findActiveByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new RuntimeException("Este usuario no tiene reconocimiento facial configurado"));

            // Verificar contra el embedding almacenado
            FaceComparisonResponse comparison = deepFaceClient.verifyFace(
                    faceLoginRequest.getFaceImageBase64(),
                    faceEncoding.getFaceEmbedding(),
                    faceEncoding.getModelName(),
                    faceEncoding.getDistanceMetric()
            );

            if (comparison.getVerified() && comparison.getSimilarity() >= 70.0) {
                usuarioAutenticado = usuario;
                maxSimilarity = comparison.getSimilarity();
            }

        } else {
            // Caso 2: Sin username - buscar en todos los usuarios con face encoding
            List<Usuario> todosUsuarios = (List<Usuario>) usuarioDao.findAll();

            for (Usuario usuario : todosUsuarios) {
                try {
                    FaceEncoding faceEncoding = faceEncodingService.findActiveByUsuarioId(usuario.getId())
                            .orElse(null);

                    if (faceEncoding == null) {
                        continue; // Este usuario no tiene face encoding
                    }

                    // Verificar contra el embedding almacenado
                    FaceComparisonResponse comparison = deepFaceClient.verifyFace(
                            faceLoginRequest.getFaceImageBase64(),
                            faceEncoding.getFaceEmbedding(),
                            faceEncoding.getModelName(),
                            faceEncoding.getDistanceMetric()
                    );

                    // Buscar el mejor match
                    if (comparison.getVerified() && 
                        comparison.getSimilarity() >= 70.0 && 
                        comparison.getSimilarity() > maxSimilarity) {
                        
                        maxSimilarity = comparison.getSimilarity();
                        usuarioAutenticado = usuario;
                    }

                } catch (Exception e) {
                    // Continuar con el siguiente usuario si hay error
                    continue;
                }
            }
        }

        // Verificar si se encontró un usuario
        if (usuarioAutenticado == null) {
            throw new RuntimeException("No se pudo verificar la identidad facial. " +
                    "Asegúrese de tener buena iluminación y que su rostro esté claramente visible.");
        }

        // Generar token JWT sin autenticación de contraseña
        // Crear Authentication manual ya que no usamos contraseña
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                usuarioAutenticado.getUsername(),
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return new AuthResponse(
                jwt,
                usuarioAutenticado.getId(),
                usuarioAutenticado.getUsername(),
                usuarioAutenticado.getCorreo(),
                usuarioAutenticado.getRol()
        );
    }
}
